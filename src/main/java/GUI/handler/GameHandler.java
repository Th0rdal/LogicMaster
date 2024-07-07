package GUI.handler;

import GUI.game.gamestate.CHECKMATE_TYPE;
import GUI.player.Algorithm.AIFile;
import GUI.controller.AlertHandler;
import GUI.controller.BoardController;
import GUI.player.Algorithm.AlgorithmHandler;
import GUI.game.*;
import GUI.player.Player;
import GUI.game.gamestate.Gamestate;
import GUI.game.gamestate.GamestateSnapshot;
import GUI.game.move.Move;
import GUI.game.timecontrol.Timecontrol;
import GUI.piece.PIECE_ID;
import GUI.piece.Piece;
import GUI.utilities.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.*;

public class GameHandler {
    private static final String START_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static final int CLOCKBASETIME = 10; //this is needed because time needs to be in tenth of a second
    private static final int WAITTIME = 50;
    private static final TimeUnit TIMEUNIT = TimeUnit.MILLISECONDS;

    // players
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean whiteTurn; // true if it is the white turn

    // other needed instances
    private BoardController boardController;
    private Gamestate gamestate;
    private Timecontrol timecontrol;
    private AlgorithmHandler algorithmHandler;
    private CyclicBarrier barrier = new CyclicBarrier(2);
    private Semaphore flagLock = new Semaphore(1);
    private final BlockingQueue<Move> moveQueue = new LinkedBlockingQueue<>(1);

    // TIME VARIABLES
    private Timeline clockWhitePlayer, clockBlackPlayer; // the clock objects
    private int clockStartTime = 0; // hard coded Clock time = 10 minutes
    private int clockWhitePlayerCounter, clockBlackPlayerCounter; // counters counting down each time event (0.1 seconds) representing the displayed time remaining
    private Timeline currentTimeRunning, currentTimeStopped; // current time that is running / stopped
    private boolean isFirstMoveMade; // true only once if there is no move taken yet
    private int increment = 0;

    // snapshot history
    private ArrayList<GamestateSnapshot> snapshotHistory = new ArrayList<>();

    // internal flags
    private boolean inSnapshot = false;
    private boolean interruptFlag = false;
    private boolean gameInitialized = false;
    private int continueFromSnapshotFlag = 0; // if this is not 0, continue from the snapshot that contains the fullmoveCounter equal to this var

    public GameHandler() {
        this.gamestate = new Gamestate();
        this.gamestate.loadStartPosition();
        this.whiteTurn = true;
        this.isFirstMoveMade = true;
    }

    public void gameLoop() {
        Move move;
        this.gameInitialized = true;

        AlertHandler.showAlertAndWait(Alert.AlertType.INFORMATION, "start game?", "Press 'OK' to start the game");
        this.startClocks(true);

        do {
            try {
                if (this.whiteTurn) {
                    if (this.whitePlayer.isHuman()) {
                        this.whitePlayer.executeGetPossibleMoves(this.gamestate, this.whiteTurn);
                        Platform.runLater(() -> {
                            this.boardController.setPlayerEventHandling(this.moveQueue);
                        });
                    } else {
                        if (this.blackPlayer.isHuman()) {
                            this.boardController.resetPlayerEventHandling();
                        }
                        this.whitePlayer.executeAlgorithm(this.gamestate, this.whiteTurn ,this.moveQueue);
                    }
                } else {
                    if (this.blackPlayer.isHuman()) {
                        this.whitePlayer.executeGetPossibleMoves(this.gamestate, this.whiteTurn);
                        Platform.runLater(() -> {
                            this.boardController.setPlayerEventHandling(this.moveQueue);
                        });
                    } else {
                        if (this.whitePlayer.isHuman()) {
                            this.boardController.resetPlayerEventHandling();
                        }
                        this.blackPlayer.executeAlgorithm(this.gamestate, this.whiteTurn, this.moveQueue);
                    }
                }

                move = null;
                while (move == null) {
                    move = this.moveQueue.poll(GameHandler.WAITTIME, GameHandler.TIMEUNIT);
                    this.flagLock.acquire();
                    if (this.interruptFlag) {
                        this.saveCurrentSnapshot();
                        this.interruptFlag = false;
                    } else if (this.continueFromSnapshotFlag != 0) {
                        this.loadSnapshot(this.continueFromSnapshotFlag);
                        this.continueFromSnapshotFlag = 0;
                    }
                    this.flagLock.release();
                }

                if (move.isDraw()) {
                    Platform.runLater(() -> {
                        this.boardController.setCheckmateAlert(CHECKMATE_TYPE.DRAW, this.whiteTurn);
                    });
                    break;
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            this.gamestate.makeMove(move);

            Move finalMove = move;
            if (!this.inSnapshot) {
                Platform.runLater(() -> {
                    this.boardController.addMoveToMovelist(finalMove, this.gamestate.getFullmoveCounter());
                    this.boardController.loadPieces();
                });
            } else {
                Platform.runLater(() -> {
                    this.boardController.addMoveToMovelist(finalMove, this.gamestate.getFullmoveCounter());
                });
            }

            this.swapTurn();

            if (move.isCheckmate()) { // here because if move.isCheckmate is true, the other player is in checkmate
                Platform.runLater(() -> {
                    this.boardController.setCheckmateAlert(CHECKMATE_TYPE.CHECKMATE, whiteTurn);
                });
                break;
            } else if (move.isDraw()) {
                Platform.runLater(() -> {
                    this.boardController.setCheckmateAlert(CHECKMATE_TYPE.STALEMATE, whiteTurn);
                });
                break;
            }

            if (this.timecontrol.hasTimecontrolSpecialChanges(this.gamestate.getFullmoveCounter()-1)) {
                int[] temp = this.timecontrol.getTimecontrolChanges(this.gamestate.getFullmoveCounter()-1);
                this.clockBlackPlayerCounter += temp[0] * GameHandler.CLOCKBASETIME;
                this.clockWhitePlayerCounter += temp[0] * GameHandler.CLOCKBASETIME;
                this.increment = temp[1] * GameHandler.CLOCKBASETIME;
            }

            this.saveCurrentSnapshot();
        } while (!Thread.currentThread().isInterrupted());
    }

    public void startGame(Timecontrol timecontrol,
                          String whiteName,
                          String blackName,
                          AIFile whiteAI,
                          AIFile blackAI,
                          String fen,
                          boolean whiteSideDown) {

        this.timecontrol = timecontrol;
        this.clockStartTime = timecontrol.getStartTime() * GameHandler.CLOCKBASETIME;
        this.increment = timecontrol.getIncrement() * GameHandler.CLOCKBASETIME;

        if (Objects.equals(whiteName, "")) {
            this.setWhitePlayer(new Player(
                    false,
                    whiteAI.getPath(),
                    whiteAI.getName()
            ));
        }else {
            this.setWhitePlayer(new Player(
                    true,
                    "",
                    whiteName
            ));
        }

        if (Objects.equals(blackName, "")) {
            this.setBlackPlayer(new Player(
                    false,
                    blackAI.getPath(),
                    blackAI.getName()
            ));
        }else {
            this.setBlackPlayer(new Player(
                    true,
                    "",
                    blackName
            ));
        }

        this.gamestate = BoardConverter.loadFEN(fen);
        if (this.gamestate == null) {
            AlertHandler.showAlert(Alert.AlertType.ERROR, "Error", "The fen notation is not valid!");
           return;
        }
        this.whiteTurn = this.gamestate.getSideFromFullmoveCounter();

        Platform.runLater(() -> {
            this.boardController.loadBoard(whiteSideDown);
            this.loadedBoard();
        });
        try { // wait for ui to finish loading
            this.barrier.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        SceneHandler.getInstance().activate("board");
        this.gameLoop();
    }

    /**
     * Loads and configures the clocks
     * @param whitePlayerLabel The label to put the opponents time in
     * @param blackPlayerLabel The label to put the players time in
     */
    public void loadClocks(Label whitePlayerLabel, Label blackPlayerLabel) {
        this.clockWhitePlayerCounter = clockStartTime;
        this.clockBlackPlayerCounter = clockStartTime;

        // creates Timelines that trigger every 0.1 seconds
        this.clockWhitePlayer = new Timeline( // defines new Timeline for the opponents clock (top of board)
            new KeyFrame(Duration.seconds(0.1), event -> {
                clockWhitePlayerCounter--;
                whitePlayerLabel.setText(Calculator.getClockTimeInFormat(clockWhitePlayerCounter));
                if (clockWhitePlayerCounter == 0) {
                    Platform.runLater(() -> {
                        this.boardController.setCheckmateAlert(CHECKMATE_TYPE.TIME, false);
                    });
                }
            })
        );
        this.clockBlackPlayer = new Timeline( // defines new Timeline for the player clock (bottom of the board)
            new KeyFrame(Duration.seconds(0.1), event -> {
                clockBlackPlayerCounter--;
                blackPlayerLabel.setText(Calculator.getClockTimeInFormat(clockBlackPlayerCounter));
                if (clockBlackPlayerCounter == 0) {
                    Platform.runLater(() -> {
                        this.boardController.setCheckmateAlert(CHECKMATE_TYPE.TIME, true);
                    });
                }
            })
        );

        // changes background color of the running clock to black and text color to white
        this.clockWhitePlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Timeline.Status.PAUSED) {
                Platform.runLater(() -> {
                    clockWhitePlayerCounter += increment;
                    whitePlayerLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                    whitePlayerLabel.setText(Calculator.getClockTimeInFormat(clockWhitePlayerCounter));
                    whitePlayerLabel.setTextFill(Color.BLACK);
                });
            }
        });
        this.clockWhitePlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Timeline.Status.RUNNING) {
                whitePlayerLabel.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                whitePlayerLabel.setTextFill(Color.WHITE);
            }
        });
        this.clockBlackPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Timeline.Status.PAUSED) {
                Platform.runLater(() -> {
                    clockBlackPlayerCounter += increment;
                    blackPlayerLabel.setText(Calculator.getClockTimeInFormat(clockBlackPlayerCounter));
                    blackPlayerLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                    blackPlayerLabel.setTextFill(Color.BLACK);
                });
            }
        });
        this.clockBlackPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Timeline.Status.RUNNING) {
                blackPlayerLabel.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                blackPlayerLabel.setTextFill(Color.WHITE);
            }
        });

        //set clock time
        this.clockWhitePlayer.setCycleCount(clockStartTime);
        this.clockBlackPlayer.setCycleCount(clockStartTime);

        //sets clocks for the first time
        whitePlayerLabel.setText(Calculator.getClockTimeInFormat(clockWhitePlayerCounter));
        blackPlayerLabel.setText(Calculator.getClockTimeInFormat(clockBlackPlayerCounter));
    }

    /**
     * This starts the clock
     * @param whiteStart if true, white made the first move, black if false
     */
    public void startClocks(boolean whiteStart) {
        if (whiteStart) {
            this.currentTimeRunning = this.clockWhitePlayer;
            this.currentTimeStopped = this.clockBlackPlayer;
        } else {
            this.currentTimeRunning = this.clockBlackPlayer;
            this.currentTimeStopped = this.clockWhitePlayer;
        }
        this.currentTimeRunning.play();
    }

    /**
     * Swaps the turn and changes the running clock
     */
    private void swapTurn() {
        this.whiteTurn = !this.whiteTurn;
        this.currentTimeRunning.pause();
        this.currentTimeStopped.play();

        Timeline temp = this.currentTimeRunning;
        this.currentTimeRunning = this.currentTimeStopped;
        this.currentTimeStopped = temp;
    }

    public void saveCurrentSnapshot() {
        GamestateSnapshot snapshot = this.gamestate.getCurrentSnapshot(this.clockBlackPlayerCounter, this.clockWhitePlayerCounter, this.timecontrol);
        this.snapshotHistory.add(snapshot);
    }

    public void loadSnapshot(int snapshotNumber) {
        GamestateSnapshot currentSnapshot = this.getSnapshot(snapshotNumber);
        this.snapshotHistory.removeIf(snapshot -> snapshot.getFullmoveCounter() > snapshotNumber);
        this.gamestate = new Gamestate(currentSnapshot);
        this.clockBlackPlayerCounter = currentSnapshot.getClockWhitePlayer();
        this.clockWhitePlayerCounter = currentSnapshot.getClockBlackPlayer();
        this.whiteTurn = (currentSnapshot.getMove() == null) == currentSnapshot.isWhiteTurn();
        this.startClocks(this.whiteTurn);
        this.inSnapshot = false;
        Platform.runLater(() -> {
            this.boardController.reloadMoveHistory();
            this.boardController.loadPieces();
        });
    }

    // getter
    public boolean isCurrentPlayerHuman() {
        return this.getCurrentPlayer().isHuman();
    }

    public Player getCurrentPlayer() {
        if (this.whiteTurn) {
            return this.whitePlayer;
        }
        return this.blackPlayer;
    }

    public boolean isUsablePiece(BoardCoordinate coordinate) {
        return this.gamestate.isUsablePiece(coordinate, this.whiteTurn);
    }

    public boolean isUsablePiece(BoardCoordinate coordinate, boolean turn) {
        return this.gamestate.isUsablePiece(coordinate, turn);
    }

    public ArrayList<Move> getPossibleMovesForCoordinates(BoardCoordinate coordinate) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        if (this.whiteTurn) {
            HashMap<String, ArrayList<Move>> temp = this.whitePlayer.executeGetPossibleMoves(gamestate, true);
            Piece piece = this.gamestate.getPieceAtCoordinates(coordinate);
            if (piece == null) {
                return possibleMoves;
            }
            if (temp.containsKey(coordinate.toString())) {
                possibleMoves.addAll(temp.get(coordinate.toString()));
            }
            if (piece.getID() == PIECE_ID.KING && piece.isWhite()) {
                if (temp.containsKey("CASTLE")) {
                    possibleMoves.addAll(temp.get("CASTLE"));
                }
            }
        } else {
            HashMap<String, ArrayList<Move>> temp = this.blackPlayer.executeGetPossibleMoves(gamestate, false);
            Piece piece = this.gamestate.getPieceAtCoordinates(coordinate);
            if (temp.containsKey(coordinate.toString())) {
                possibleMoves.addAll(temp.get(coordinate.toString()));
            }
            if (piece.getID() == PIECE_ID.KING && !piece.isWhite()) {
                if (temp.containsKey("CASTLE")) {
                    possibleMoves.addAll(temp.get("CASTLE"));
                }
            }
        }
        return possibleMoves;
    }

    public ArrayList<Piece> getPieces() {
        return this.gamestate.getPieces();
    }

    public boolean isTurnWhite() {
        return this.whiteTurn;
    }



    // setter
    public void loadedBoard() {
        try {
            this.barrier.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setWhitePlayer(Player player) {
        this.whitePlayer = player;
    }
    public void setBlackPlayer(Player player) {this.blackPlayer = player;}

    public void setInterruptFlag() {
        try {
            this.flagLock.acquire();
            this.interruptFlag = true;
            this.flagLock.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void setContinueFromSnapshotFlag(int number) {
        try {
            this.flagLock.acquire();
            this.continueFromSnapshotFlag = number;
            this.flagLock.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public GamestateSnapshot getSnapshot(int moveNumber) {
        for (GamestateSnapshot snapshot : this.snapshotHistory) {
            if (snapshot.getFullmoveCounter() == moveNumber) {
                if (moveNumber != this.snapshotHistory.size()) {
                    this.inSnapshot = true;
                } else {
                    this.inSnapshot = false;
                }
                return snapshot;
            }
        }
        return null;
    }

    public int getFullmoveClock() {
        return this.gamestate.getFullmoveCounter();
    }

    public BoardController getController() {
        return this.boardController;
    }

    public boolean isInSnapshot() {return this.inSnapshot;}

    public boolean isUsablePieceSnapshot(BoardCoordinate coordinate, int moveCounter) {
        GamestateSnapshot gamestateSnapshot = this.getSnapshot(moveCounter);
        Piece piece = null;
        for (Piece temp : gamestateSnapshot.getPieces()) {
            if (temp.getCoordinates().equals(coordinate)) {
                piece = temp;
                break;
            }
        }
        if (piece == null) {
            return false;
        }
        boolean tempBool = (moveCounter + 1) % 2 == 1;
        return piece.isWhite() == tempBool;
    }

    public Player getPlayer(boolean whitePlayer) {
        return whitePlayer ? this.whitePlayer : this.blackPlayer;
    }

    public void setController(BoardController boardController) {
        this.boardController = boardController;
        this.boardController.setGameHandler(this);
    }

    public boolean isGameInitialized() {return this.gameInitialized;}

    public ArrayList<GamestateSnapshot> getSnapshotHistory() {return this.snapshotHistory;}
}
