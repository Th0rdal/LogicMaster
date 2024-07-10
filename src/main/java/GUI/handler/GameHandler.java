package GUI.handler;

import GUI.exceptions.DatabaseConnectionException;
import GUI.exceptions.GameLoadedIncorrectlyException;
import GUI.exceptions.ObjectInterruptedException;
import GUI.game.gamestate.CHECKMATE_TYPE;
import GUI.game.gamestate.GAMESTATUS;
import GUI.player.algorithm.AIFile;
import GUI.controller.AlertHandler;
import GUI.controller.BoardController;
import GUI.game.*;
import GUI.player.Player;
import GUI.game.gamestate.Gamestate;
import GUI.game.gamestate.GamestateSnapshot;
import GUI.game.move.Move;
import GUI.game.timecontrol.Timecontrol;
import GUI.piece.PIECE_ID;
import GUI.piece.Piece;
import GUI.utilities.*;
import database.ChessGame;
import database.Database;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.*;

public class GameHandler {
    private static final int CLOCKBASETIME = 10; //this is needed because time needs to be in tenth of a second
    private static final int WAITTIME = 50;
    private static final TimeUnit TIMEUNIT = TimeUnit.MILLISECONDS;
    private static final int THREAD_SLEEP_TIME = 100;

    // players
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean whiteTurn; // true if it is the white turn

    // other needed instances
    private BoardController boardController;
    private Gamestate gamestate;
    private Timecontrol timecontrol;
    private final CyclicBarrier barrier = new CyclicBarrier(2);
    private final Semaphore flagLock = new Semaphore(1);
    private final BlockingQueue<Move> moveQueue = new LinkedBlockingQueue<>(1);
    CompletableFuture<Boolean> future = new CompletableFuture<>();

    // TIME VARIABLES
    private Timeline clockWhitePlayer = null, clockBlackPlayer = null; // the clock objects
    private int clockStartTime = 0; // hard coded Clock time = 10 minutes
    private int clockWhitePlayerCounter = -1;
    private int clockBlackPlayerCounter = -1; // counters counting down each time event (0.1 seconds) representing the displayed time remaining
    private Timeline currentTimeRunning, currentTimeStopped; // current time that is running / stopped
    private int increment = 0;

    // snapshot history
    private ArrayList<GamestateSnapshot> snapshotHistory = new ArrayList<>();
    private GamestateSnapshot startGamestateSnapshot = null; // needed for 3-fold repetition

    // internal flags
    private boolean inSnapshot = false;
    private boolean interruptFlag = false;
    private boolean gameInitialized = false;
    private boolean shutdownFlag = false;
    private GAMESTATUS gamestatus;
    private int continueFromSnapshotFlag = 0; // if this is not 0, continue from the snapshot that contains the fullmoveCounter equal to this var

    private String startFen = "";

    public GameHandler() {
        this.whiteTurn = true;
    }

    public void gameLoop() {
        Move move;
        this.gameInitialized = true;

        Platform.runLater(this.boardController::resetPlayerEventHandling);
        AlertHandler.showAlertAndWait(Alert.AlertType.INFORMATION, "start game?", "Press 'OK' to start the game");
        this.startClocks(this.whiteTurn);
        this.gamestatus = GAMESTATUS.ONGOING;

        do {
            try {
                if (this.whiteTurn) {
                    if (this.whitePlayer.isHuman()) {
                        this.whitePlayer.executeGetPossibleMoves(this.gamestate, this.whiteTurn); // only loads the possible moves. does not actually use results
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
                        this.toggleRunningTime();
                        this.saveCurrentSnapshot();
                        while (this.interruptFlag) {
                            this.flagLock.release();
                            Thread.sleep(GameHandler.THREAD_SLEEP_TIME);
                            this.flagLock.acquire();
                        }
                        if (this.shutdownFlag) {
                            this.shutdownFlag = false;
                            this.future.complete(true);
                            this.flagLock.release();
                            return;
                        }
                        this.loadBoard();
                        this.flagLock.release();
                        this.toggleRunningTime();
                        continue;
                    } else if (this.continueFromSnapshotFlag != 0) {
                        this.loadSnapshot(this.continueFromSnapshotFlag);
                        this.continueFromSnapshotFlag = 0;
                    } else if (this.shutdownFlag) {
                        this.shutdownFlag = false;
                        this.future.complete(true);
                        this.flagLock.release();
                        return;
                    }
                    this.flagLock.release();
                }

            } catch (InterruptedException e) {
                this.saveCurrentInDatabase();
                AlertHandler.throwError();
                throw new ObjectInterruptedException("The game loop was interrupted unexpectedly!", e);
            }


            this.gamestate.makeMove(move);

            if (!this.getPlayer(this.whiteTurn).isHuman()) {
                Platform.runLater(this.boardController::afterMove);
            }

            Move finalMove = move;
            if (!this.inSnapshot) {
                Platform.runLater(() -> {
                    this.boardController.addMoveToMovelist(finalMove, this.gamestate.getOldFullmoveCounter());
                    this.boardController.loadPieces();
                });
            } else {
                Platform.runLater(() -> {
                    this.boardController.addMoveToMovelist(finalMove, this.gamestate.getOldFullmoveCounter());
                });
            }

            this.swapTurn();

            if (this.timecontrol.hasTimecontrolSpecialChanges(this.gamestate.getFullmoveCounter()-1)) {
                int[] temp = this.timecontrol.getTimecontrolChanges(this.gamestate.getFullmoveCounter()-1);
                this.clockBlackPlayerCounter += temp[0] * GameHandler.CLOCKBASETIME;
                this.clockWhitePlayerCounter += temp[0] * GameHandler.CLOCKBASETIME;
                this.increment = temp[1] * GameHandler.CLOCKBASETIME;
            }

            this.saveMoveSnapshot(move);

            if (move.isCheckmate()) { // here because if move.isCheckmate is true, the other player is in checkmate
                this.gamestatus = whiteTurn ? GAMESTATUS.CHECKMATE_WHITE : GAMESTATUS.CHECKMATE_BLACK;
                Platform.runLater(() -> {
                    this.boardController.setCheckmateAlert(CHECKMATE_TYPE.CHECKMATE, whiteTurn);
                });
                break;
            } else if (this.checkMove3FoldRepetition()) {
                this.gamestatus = GAMESTATUS.DRAW;
                CHECKMATE_TYPE type = CHECKMATE_TYPE.THREEFOLD_REPETITION;
                Platform.runLater(() -> {
                    this.boardController.setCheckmateAlert(type, this.whiteTurn);
                });
                break;
            }else if (move.isDraw()) {
                this.gamestatus = move.isDrawOffered() ? GAMESTATUS.DRAW : GAMESTATUS.STALEMATE;
                CHECKMATE_TYPE type = move.isDrawOffered() ? CHECKMATE_TYPE.DRAW : CHECKMATE_TYPE.STALEMATE;
                Platform.runLater(() -> {
                    this.boardController.setCheckmateAlert(type, this.whiteTurn);
                });
                break;
            }

        } while (!Thread.currentThread().isInterrupted());
    }

    public void startGame(Timecontrol timecontrol,
                          String whiteName,
                          String blackName,
                          AIFile whiteAI,
                          AIFile blackAI,
                          String fen) {

        this.timecontrol = timecontrol;
        this.clockStartTime = timecontrol.getStartTime() * GameHandler.CLOCKBASETIME;
        this.clockWhitePlayerCounter = -1;
        this.clockBlackPlayerCounter = -1;
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
        this.startFen = fen.trim();

        if (this.gamestate == null) {
            AlertHandler.showAlert(Alert.AlertType.ERROR, "Error", "The fen notation is not valid!");
            return;
        }
        this.startGamestateSnapshot = this.gamestate.getStartSnapshot(this.clockWhitePlayerCounter, this.clockBlackPlayerCounter);

        this.whiteTurn = this.gamestate.getSideFromFullmoveCounter();
        this.snapshotHistory = new ArrayList<>();

        this.boardController.reloadMoveHistory();
        this.moveQueue.clear();

        this.inSnapshot = false;
        this.gameInitialized = false;

        loadBoard();
        this.gameLoop();
    }

    private void loadBoard() {
        Platform.runLater(() -> {
            this.boardController.loadBoard();
            this.loadedBoard();
        });
        try {
            this.barrier.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            AlertHandler.throwError();
            throw new ObjectInterruptedException("Barrier interrupted unexpectedly.", e);
        }
        SceneHandler.getInstance().activate("board");
    }

    /**
     * Loads and configures the clocks
     * @param whitePlayerLabel The label to put the opponents time in
     * @param blackPlayerLabel The label to put the players time in
     */
    public boolean loadClocks(Label whitePlayerLabel, Label blackPlayerLabel) {
        if (!this.timecontrol.isActive()) {
            return false;
        }
        if (this.clockWhitePlayerCounter == -1) {
            this.clockWhitePlayerCounter = clockStartTime;
        }
        if (this.clockBlackPlayerCounter == -1) {
            this.clockBlackPlayerCounter = clockStartTime;
        }

        if (this.clockWhitePlayer == null) {
            // creates Timelines that trigger every 0.1 seconds
            this.clockWhitePlayer = new Timeline( // defines new Timeline for the opponents clock (top of board)
                    new KeyFrame(Duration.seconds(0.1), event -> {
                        clockWhitePlayerCounter--;
                        whitePlayerLabel.setText(Calculator.getClockTimeInFormat(clockWhitePlayerCounter));
                        if (clockWhitePlayerCounter == 0) {
                            this.gamestatus = GAMESTATUS.CHECKMATE_TIME_WHITE;
                            Platform.runLater(() -> {
                                this.boardController.setCheckmateAlert(CHECKMATE_TYPE.TIME, false);
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
            //set clock time
            this.clockWhitePlayer.setCycleCount(Animation.INDEFINITE);
            //sets clocks for the first time
            whitePlayerLabel.setText(Calculator.getClockTimeInFormat(clockWhitePlayerCounter));
        }

        if (this.clockBlackPlayer == null) {
            this.clockBlackPlayer = new Timeline( // defines new Timeline for the player clock (bottom of the board)
                new KeyFrame(Duration.seconds(0.1), event -> {
                    clockBlackPlayerCounter--;
                    blackPlayerLabel.setText(Calculator.getClockTimeInFormat(clockBlackPlayerCounter));
                    if (clockBlackPlayerCounter == 0) {
                        this.gamestatus = GAMESTATUS.CHECKMATE_TIME_BLACK;
                        Platform.runLater(() -> {
                            this.boardController.setCheckmateAlert(CHECKMATE_TYPE.TIME, true);
                        });
                    }
                })
            );
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
            this.clockBlackPlayer.setCycleCount(Animation.INDEFINITE);
            blackPlayerLabel.setText(Calculator.getClockTimeInFormat(clockBlackPlayerCounter));
        }
        return true;
    }

    private boolean checkMove3FoldRepetition() {
        int counter = 0;
        ArrayList<GamestateSnapshot> tempSnapshots = new ArrayList<>(this.snapshotHistory);
        tempSnapshots.add(this.startGamestateSnapshot);

        for (GamestateSnapshot snapshot : tempSnapshots) {
            if (snapshot.getPieces().size() != this.gamestate.getPieces().size()) {
                continue;
            }
            boolean flag = true;
            for (Piece piece : this.gamestate.getPieces()) {
                if (!snapshot.getPieces().contains(piece)) {
                    flag = false;
                    break;
                }
            }
            if (!(snapshot.isWhiteTurn() == this.gamestate.isWhiteTurn())) {
                flag = false;
            /*} else if (!(snapshot.canWhiteQCastle() == this.gamestate.canWhiteQCastle())
                || !(snapshot.canWhiteKCastle() == this.gamestate.canWhiteKCastle())
                || !(snapshot.canBlackKCastle() == this.gamestate.canBlackKCastle())
                || !(snapshot.canBlackQCastle() == this.gamestate.canBlackQCastle())) {

                flag = false;
            } else if (!(snapshot.getEnPassantCoordinates().equals(this.gamestate.getEnPassantCoordinates()))) {
                flag = false;*/
            }

            if (flag) {
                counter++;
            }
        }
        return counter >= 3;
    }

    /**
     * This starts the clock
     * @param whiteStart if true, white made the first move, black if false
     */
    public void startClocks(boolean whiteStart) {
        if (!this.timecontrol.isActive()) {
            return;
        }
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

        if (this.timecontrol.isActive()) {
            this.currentTimeRunning.pause();
            this.currentTimeStopped.play();

            Timeline temp = this.currentTimeRunning;
            this.currentTimeRunning = this.currentTimeStopped;
            this.currentTimeStopped = temp;
        }
    }

    public void saveCurrentSnapshot() {
        GamestateSnapshot snapshot = this.gamestate.getSnapshot(this.clockBlackPlayerCounter, this.clockWhitePlayerCounter);
        this.snapshotHistory.add(snapshot);
    }

    public void saveMoveSnapshot(Move move) {
        GamestateSnapshot snapshot = this.gamestate.getSnapshot(move, this.clockBlackPlayerCounter, this.clockWhitePlayerCounter);
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

    public void saveCurrentInDatabase() {
        ArrayList<Byte> byteMoveList = new ArrayList<>();
        ArrayList<Integer> timeList = new ArrayList<>();

        for (GamestateSnapshot snapshot : this.snapshotHistory) {
            if (snapshot.getMove() != null) {
                timeList.add(snapshot.getClockChanged());
                byte[] byteMove = snapshot.getMove().convertToByte();
                for (Byte tempByte : byteMove) {
                    byteMoveList.add(tempByte);
                }
            }
        }

        byte[] byteArray = new byte[byteMoveList.size()];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = byteMoveList.get(i);
        }

        byte[] timeArray = new byte[timeList.size()*4];
        int counter = 0;
        for (int i = 0; i < timeList.size(); i++) {
            for (Byte tempByte : ByteBuffer.allocate(4).putInt(timeList.get(i)).array()) {
                timeArray[counter] = tempByte;
                counter++;
            }
        }

        String endFen = BoardConverter.createFEN(gamestate, this.isTurnWhite());
        ChessGame game = new ChessGame(
                byteArray,
                timeArray,
                this.timecontrol.toString(),
                this.whitePlayer.getName(),
                this.blackPlayer.getName(),
                this.whitePlayer.getPath(),
                this.blackPlayer.getPath(),
                this.gamestatus,
                this.whiteTurn,
                this.startFen,
                endFen);

        try {
            Database.getInstance().getDao().create(game);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("There was an error when trying to save a game in the database.", e);
        }
    }

    public void loadFromDatabaseAndStartGame(ChessGame game) {
        this.snapshotHistory = new ArrayList<>();
        this.setWhitePlayer(new Player(game.getWhitePlayerPath().isEmpty(), game.getWhitePlayerPath(), game.getWhitePlayerName()));
        this.setBlackPlayer(new Player(game.getBlackPlayerPath().isEmpty(), game.getBlackPlayerPath(), game.getBlackPlayerName()));

        this.timecontrol = new Timecontrol(game.getTimeControl());
        this.clockStartTime = timecontrol.getStartTime() * GameHandler.CLOCKBASETIME;
        this.clockWhitePlayerCounter = -1;
        this.clockBlackPlayerCounter = -1;
        this.increment = this.timecontrol.getIncrement() * CLOCKBASETIME;

        this.startFen = game.getStartingFen();
        this.gamestatus = game.getGameStatus();
        this.gamestate = BoardConverter.loadFEN(startFen);
        ArrayList<Move> moveList = Move.convertByteMovesToArrayList(game.getMoves());
        ArrayList<Integer> timeList = Timecontrol.convertByteTimeToArrayList(game.getTime());
        this.executeMoveList(moveList, timeList);
        if (!(Objects.equals(BoardConverter.loadFEN(game.getEndFen()), this.gamestate))) {
            AlertHandler.throwError();
            throw new GameLoadedIncorrectlyException("The end fen string does not equal the calculated end gamestate");
        }
        if (this.whiteTurn != game.isWhiteTurn()) {
            AlertHandler.throwError();
            throw new GameLoadedIncorrectlyException("The current move side does not equal the calculated current move side");
        }

        this.loadBoard();
        this.gameLoop();
    }

    private void executeMoveList(ArrayList<Move> moveList, ArrayList<Integer> timeList) {
        for (int i = 0; i < moveList.size(); i++) {
            if (this.gamestate.isWhiteTurn()) {
                this.clockWhitePlayerCounter = timeList.get(i);
            } else {
                this.clockBlackPlayerCounter = timeList.get(i);
            }
            this.gamestate.makeMove(moveList.get(i));
            this.saveMoveSnapshot(moveList.get(i));
        }
        this.whiteTurn = this.gamestate.isWhiteTurn();
        Platform.runLater(() -> {
            this.boardController.reloadMoveHistory();
            this.boardController.loadPieces();
        });
    }

    public boolean isUsablePiece(BoardCoordinate coordinate) {
        return this.gamestate.isUsablePiece(coordinate, this.whiteTurn);
    }

    public boolean isUsablePiece(BoardCoordinate coordinate, boolean turn) {
        return this.gamestate.isUsablePiece(coordinate, turn);
    }

    public ArrayList<Move> getPossibleMovesForCoordinates(BoardCoordinate coordinate) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        HashMap<String, ArrayList<Move>> temp = this.getPlayer(this.whiteTurn).executeGetPossibleMoves(this.gamestate, this.whiteTurn);
        if (!temp.get("DRAW").isEmpty()) {
            try {
                this.moveQueue.put(temp.get("DRAW").get(0));
            } catch (InterruptedException e) {
                AlertHandler.throwError();
                throw new ObjectInterruptedException("Move queue interrupted unexpectedly", e);
            }
        }
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
            AlertHandler.throwError();
            throw new ObjectInterruptedException("Barrier interrupted unexpectedly.", e);
        }
    }

    public void setGamestatusDrawAgreed() {
        this.gamestatus = GAMESTATUS.DRAW;
    }

    public void setGamestatusCheckmateTime(boolean whiteTurn) {
        this.gamestatus = whiteTurn ? GAMESTATUS.CHECKMATE_TIME_WHITE : GAMESTATUS.CHECKMATE_TIME_BLACK;
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
            AlertHandler.throwError();
            throw new ObjectInterruptedException("semaphore interrupted unexpectedly", e);
        }
    }
    public void setContinueFromSnapshotFlag(int number) {
        try {
            this.flagLock.acquire();
            this.continueFromSnapshotFlag = number;
            this.flagLock.release();
        } catch (InterruptedException e) {
            AlertHandler.throwError();
            throw new ObjectInterruptedException("semaphore interrupted unexpectedly", e);
        }
    }

    public GamestateSnapshot getSnapshot(int moveNumber) {
        for (GamestateSnapshot snapshot : this.snapshotHistory) {
            if (snapshot.getFullmoveCounter() == moveNumber) {
                this.inSnapshot = moveNumber != this.snapshotHistory.size();
                return snapshot;
            }
        }
        return null;
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

    public boolean canDrawFiftyMoves() {
        return this.gamestate.getHalfmoveCounter() >= 50;
    }

    public void resetInterruptFlag() {
        try {
            this.flagLock.acquire();
        } catch (InterruptedException e) {
            AlertHandler.throwError();
            throw new ObjectInterruptedException("semaphore interrupted unexpectedly", e);
        }
        this.interruptFlag = false;
        this.flagLock.release();
    }

    public void setShutdownFlag() {
        try {
            this.flagLock.acquire();
        } catch (InterruptedException e) {
            AlertHandler.throwError();
            throw new ObjectInterruptedException("semaphore interrupted unexpectedly", e);
        }
        this.shutdownFlag = true;
        this.flagLock.release();
    }

    public void waitForOldThreadShutdown() {
        try {
            this.future.get();
            this.future = this.future.newIncompleteFuture();
        } catch (InterruptedException | ExecutionException e) {
            AlertHandler.throwError();
            throw new ObjectInterruptedException("future interrupted unexpectedly", e);
        }
    }

    private void toggleRunningTime() {
        if (!this.timecontrol.isActive()) {
            return;
        }
        if (this.currentTimeRunning.getStatus() == Animation.Status.PAUSED) {
            this.currentTimeRunning.play();
        } else if (this.currentTimeRunning.getStatus() == Animation.Status.RUNNING) {
            this.currentTimeRunning.pause();
        }
    }
}
