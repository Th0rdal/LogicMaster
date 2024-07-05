package GUI;

import GUI.Player.Player;
import GUI.piece.PIECE_ID;
import GUI.piece.Piece;
import GUI.utilities.AlgorithmHandler;
import GUI.utilities.BoardCoordinate;
import GUI.utilities.Calculator;
import GUI.utilities.Move;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameHandler {
    private static final String START_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    // players
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean whiteTurn; // true if it is the white turn

    private Controller controller;
    private Gamestate gamestate;

    private AlgorithmHandler algorithmHandler;

    // TIME VARIABLES
    private Timeline clockOpponent, clockPlayer; // the clock objects
    private static final int CLOCKTIME = 1 * 60 * 60 * 10; // hard coded Clock time = 10 minutes
    private int clockOpponentCounter, clockPlayerCounter; // counters counting down each time event (0.1 seconds) representing the displayed time remaining
    private Timeline currentTimeRunning, currentTimeStopped; // current time that is running / stopped
    private boolean isFirstMoveMade = true; // true only once if there is no move taken yet

    // snapshot history
    private ArrayList<GamestateSnapshot> snapshotHistory = new ArrayList<>();

    private final BlockingQueue<Move> moveQueue = new LinkedBlockingQueue<>();

    private boolean inSnapshot = false;

    public GameHandler() {
        this.gamestate = new Gamestate();
        this.gamestate.loadStartPosition();
        this.whiteTurn = true;
        this.isFirstMoveMade = true;
    }

    public void gameLoop() {
        Move move;
        do {

            try {
                if (whiteTurn) {
                    if (this.whitePlayer.isHuman()) {
                        this.whitePlayer.executeGetPossibleMoves(this.gamestate, this.whiteTurn);
                        Platform.runLater(() -> {
                            this.controller.setPlayerEventHandling(this.moveQueue);
                        });
                    } else {
                        if (this.blackPlayer.isHuman()) {
                            this.controller.resetPlayerEventHandling();
                        }
                        this.whitePlayer.executeAlgorithm(this.gamestate, this.whiteTurn ,this.moveQueue);
                    }
                } else {
                    if (this.blackPlayer.isHuman()) {
                        this.whitePlayer.executeGetPossibleMoves(this.gamestate, this.whiteTurn);
                        Platform.runLater(() -> {
                            this.controller.setPlayerEventHandling(this.moveQueue);
                        });
                    } else {
                        if (this.whitePlayer.isHuman()) {
                            this.controller.resetPlayerEventHandling();
                        }
                        this.blackPlayer.executeAlgorithm(this.gamestate, this.whiteTurn, this.moveQueue);
                    }
                }

                move = this.moveQueue.take();

                if (this.isFirstMoveMade) {
                    this.isFirstMoveMade = false;
                    this.startClocks(true);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            GamestateSnapshot snapshot = this.gamestate.makeMove(move, this.whiteTurn, this.clockPlayerCounter, this.clockOpponentCounter);
            this.snapshotHistory.add(snapshot);
            Move finalMove = move;
            if (!this.inSnapshot) {
                Platform.runLater(() -> {
                    this.controller.addMoveToMovelist(finalMove);
                    this.controller.loadPieces();
                });
            } else {
                Platform.runLater(() -> {
                    this.controller.addMoveToMovelist(finalMove);
                });
            }
            this.swapTurn();

        } while (!move.isDraw() && !move.isCheckmate());
    }

    /**
     * Loads and configures the clocks
     * @param opponentLabel The label to put the opponents time in
     * @param playerLabel The label to put the players time in
     */
    public void loadClocks(Label opponentLabel, Label playerLabel) {
        this.clockOpponentCounter = CLOCKTIME;
        this.clockPlayerCounter = CLOCKTIME;

        // creates Timelines that trigger every 0.1 seconds
        this.clockOpponent = new Timeline( // defines new Timeline for the opponents clock (top of board)
            new KeyFrame(Duration.seconds(0.1), event -> {
                clockOpponentCounter--;
                opponentLabel.setText(Calculator.getClockTimeInFormat(clockOpponentCounter));
            })
        );
        this.clockPlayer = new Timeline( // defines new Timeline for the player clock (bottom of the board)
            new KeyFrame(Duration.seconds(0.1), event -> {
                clockPlayerCounter--;
                playerLabel.setText(Calculator.getClockTimeInFormat(clockPlayerCounter));
            })
        );

        // changes background olor of the running clock to black and text color to white
        this.clockOpponent.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Timeline.Status.PAUSED) {
                opponentLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                opponentLabel.setTextFill(Color.BLACK);
            }
        });
        this.clockOpponent.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Timeline.Status.RUNNING) {
                opponentLabel.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                opponentLabel.setTextFill(Color.WHITE);
            }
        });
        this.clockPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Timeline.Status.PAUSED) {
                playerLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                playerLabel.setTextFill(Color.BLACK);
            }
        });
        this.clockPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Timeline.Status.RUNNING) {
                playerLabel.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
                playerLabel.setTextFill(Color.WHITE);
            }
        });

        //set clock time
        this.clockOpponent.setCycleCount(CLOCKTIME);
        this.clockPlayer.setCycleCount(CLOCKTIME);

        //sets clocks for the first time
        opponentLabel.setText(Calculator.getClockTimeInFormat(clockOpponentCounter));
        playerLabel.setText(Calculator.getClockTimeInFormat(clockPlayerCounter));
    }

    /**
     * This starts the clock
     * @param whiteStart if true, white made the first move, black if false
     */
    public void startClocks(boolean whiteStart) {
        if (whiteStart) {
            this.currentTimeRunning = this.clockPlayer;
            this.currentTimeStopped = this.clockOpponent;
        } else {
            this.currentTimeRunning = this.clockOpponent;
            this.currentTimeStopped = this.clockPlayer;
        }
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

    // getter
    public boolean isCurrentPlayerHuman() {
        return this.getCurrentPlayer().isHuman();
    }

    public Move getMoveFromPossibleMoves(ArrayList<Move> possibleMoves, BoardCoordinate endPosition) {

        return null;
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

    public ArrayList<Move> getPossibleMovesForCoordinates(BoardCoordinate coordinate) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        if (this.whiteTurn) {
            HashMap<String, ArrayList<Move>> temp = this.whitePlayer.executeGetPossibleMoves(gamestate, true);
            Piece piece = this.gamestate.getPieceAtCoordinates(coordinate);
            possibleMoves.addAll(temp.get(coordinate.toString()));
            if (piece.getID() == PIECE_ID.KING && piece.isWhite()) {
                ArrayList<Move> castle = temp.get("CASTLE");
                possibleMoves.addAll(castle);
            }
        } else {
            HashMap<String, ArrayList<Move>> temp = this.blackPlayer.executeGetPossibleMoves(gamestate, false);
            Piece piece = this.gamestate.getPieceAtCoordinates(coordinate);
            possibleMoves.addAll(temp.get(coordinate.toString()));
            if (piece.getID() == PIECE_ID.KING && !piece.isWhite()) {
                ArrayList<Move> castle = temp.get("CASTLE");
                possibleMoves.addAll(castle);
            }
        }
        return possibleMoves;
    }

    public ArrayList<Piece> getPieces() {
        return this.gamestate.getPieces();
    }



    // setter
    public void setWhitePlayer(Player player) {
        this.whitePlayer = player;
    }
    public void setBlackPlayer(Player player) {this.blackPlayer = player;}

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
        return this.gamestate.getFullmoveClock();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
