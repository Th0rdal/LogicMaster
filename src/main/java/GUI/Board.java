package GUI;

import GUI.piece.*;
import GUI.utilities.BoardCoordinate;
import GUI.utilities.Calculator;
import GUI.utilities.Move;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Represents the chess board (not visual) and handles all calculations.
 */
public class Board {

    private static final String START_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static Board instance = null;

    // hard coded Clock time = 10 minutes
    private static final int CLOCKTIME = 1 * 60 * 60 * 10;

    private ArrayList<Piece> pieces = new ArrayList<>(); // contains all pieces currently on the board

    // SPECIAL MOVES AND COUNTER VARIABLES
    private boolean whiteTurn; // true if it is the white turn
    // true if castle is possible (ONLY TRUE IF GENERAL CASTLING IS POSSIBLE. DOES NOT LOOK AT THE BOARD AT ALL)
    private boolean whiteQCastle = true, whiteKCastle = true, blackQCastle = true, blackKCastle = true;
    private BoardCoordinate enPassantCoordinates; // the coordinates of a field that can be taken by en Passant
    private int halfmoveClock = 0; // half move counts moves since last pawn capture or pawn move
    private int fullmoveClock = 0; // full move clock counts the total amount of moves
    private boolean firstMoveMade = true; // true only once if there is no move taken yet

    // TIME VARIABLES
    private Timeline clockOpponent, clockPlayer; // the clock objects
    // counters counting down each time event (0.1 seconds) representing the displayed time remaining
    private int clockOpponentCounter, clockPlayerCounter;
    // current time that is running / stopped
    private Timeline currentTimeRunning, currentTimeStopped;

    // MOVE HISTORY
    private ArrayList<Move> moveHistory; // represents the moves taken

    /**
     * This function handles everything that is needed to be done, when a move was made.
     * TODO should take Move as parameter because move is chosen from Move list
     * @param startCoordinates
     * @param endCoordinate
     */
    public void makeMove(BoardCoordinate startCoordinates, BoardCoordinate endCoordinate) {
        Piece piece = this.getPieceAtCoordinates(startCoordinates);
        if (piece == null) {
            //TODO make this a useful Error
            throw new RuntimeException("UNEXPECTED. THIS SHOULD NEVER HAPPEN. piece is null in makeMove");
        }
        piece.makeMove(endCoordinate);
        if (this.firstMoveMade) {
            this.firstMoveMade = false;
            this.startClocks(true);
        }
        this.swapTurn();

    }

    /**
     * Checks if the there is a piece on the given coordinates
     * @param coordinates The coordinates to check
     * @return true if there is a piece, else false
     */
    public boolean hasPiece(BoardCoordinate coordinates) {
        Piece piece = this.getPieceAtCoordinates(coordinates);
        if (piece == null) { // return false if there is no piece at this coordinates
            return false;
        }
        return true;
    }

    /**
     * Checks if there is a usable piece at the current location. Usable pieces is defined as a piece that
     * can be moved in the current turn. So all white pieces are usable pieces if it is currently white's turn.
     * @param coordinates The coordinates to check
     * @return true if the piece is usable, else false
     */
    public boolean isUsablePiece(BoardCoordinate coordinates) {
        /*
         * This function checks if the piece at the given coordinates is usable. This means that the piece belongs
         * to the color whose turn it currently is
         */

        Piece piece = this.getPieceAtCoordinates(coordinates);
        if (piece == null) { // return false if there is no piece at this coordinates
            return false;
        }

        return piece.isWhite() == this.whiteTurn;

    }

    /**
     * Clears pieces and moveHistory
     */
    public void clearBoard() {
        this.pieces = new ArrayList<>();
        //create new moveHistory
        this.moveHistory = new ArrayList<>();
    }

    /**
     * loads a fresh new default board and resets all values needed: This is equivalent to the FEN notation
     * that is saved in the variable START_POSITION
     */
    public void loadStartPosition() {
        this.clearBoard();

        // add all black pieces
        for (int i = 1; i <= 8; i++) {
            pieces.add(new Pawn(new BoardCoordinate(i, 7), false));
        }
        pieces.add(new Rook(new BoardCoordinate(1, 8), false));
        pieces.add(new Rook(new BoardCoordinate(8, 8), false));
        pieces.add(new King(new BoardCoordinate(5, 8), false));
        pieces.add(new Queen(new BoardCoordinate(4, 8), false));
        pieces.add(new Knight(new BoardCoordinate(2, 8), false));
        pieces.add(new Knight(new BoardCoordinate(7, 8), false));
        pieces.add(new Bishop(new BoardCoordinate(3, 8), false));
        pieces.add(new Bishop(new BoardCoordinate(6, 8), false));

        // add all white pieces
        for (int i = 1; i <= 8; i++) {
            pieces.add(new Pawn(new BoardCoordinate(i, 2), true));
        }
        pieces.add(new Rook(new BoardCoordinate(1, 1), true));
        pieces.add(new Rook(new BoardCoordinate(8, 1), true));
        pieces.add(new King(new BoardCoordinate(5, 1), true));
        pieces.add(new Queen(new BoardCoordinate(4, 1), true));
        pieces.add(new Knight(new BoardCoordinate(2, 1), true));
        pieces.add(new Knight(new BoardCoordinate(7, 1), true));
        pieces.add(new Bishop(new BoardCoordinate(3, 1), true));
        pieces.add(new Bishop(new BoardCoordinate(6, 1), true));

        // set start turn to white
        this.whiteTurn = true;

        // all Castling possible
        this.whiteQCastle = true;
        this.whiteKCastle = true;
        this.blackQCastle = true;
        this.blackKCastle = true;
        this.enPassantCoordinates = null;
        this.halfmoveClock = 0;
        this.fullmoveClock = 0;
        this.firstMoveMade = true;
    }

    /**
     * removes a piece from the piece list.
     * @param coordinates: Coordinates of the piece to remove
     * @return true if piece was removed, else false
     */
    public boolean removePiece(BoardCoordinate coordinates) {
        Piece piece = this.getPieceAtCoordinates(coordinates);
        if (piece != null) {
            this.pieces.remove(piece);
            return true;
        }
        return false;
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


    public void loadConfiguration(
            ArrayList<Piece> pieces,
            boolean turn,
            boolean whiteKCastle,
            boolean whiteQCastle,
            boolean blackKCastle,
            boolean blackQCastle,
            String enPassant,
            int halfmoveClock,
            int fullmoveClock) {

        this.clearBoard();

        this.pieces = pieces;
        this.whiteTurn = turn;
        this.whiteKCastle = whiteKCastle;
        this.whiteQCastle = whiteQCastle;
        this.blackKCastle = blackKCastle;
        this.blackQCastle = blackQCastle;
        this.enPassantCoordinates = new BoardCoordinate(enPassant);
        this.halfmoveClock = halfmoveClock;
        this.fullmoveClock = fullmoveClock;
    }

    public void setPieces(ArrayList<Piece> pieces) {
        this.pieces = pieces;
    }

    public void setTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public void setCastles(boolean whiteQCastle,
                           boolean whiteKCastle,
                           boolean blackQCastle,
                           boolean blackKCastle) {

        this.whiteQCastle = whiteQCastle;
        this.whiteKCastle = whiteKCastle;
        this.blackQCastle = blackQCastle;
        this.blackKCastle = blackKCastle;
    }

    public void setEnPassantCoordinates(BoardCoordinate enPassantCoordinates) {
        this.enPassantCoordinates = enPassantCoordinates;
    }

    public void setHalfmoveClock(int halfmoveClock) {
        this.halfmoveClock = halfmoveClock;
    }

    public void setFullmoveClock(int fullmoveClock) {
        this.fullmoveClock = fullmoveClock;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public boolean isWhiteQCastle() {
        return whiteQCastle;
    }

    public boolean isWhiteKCastle() {
        return whiteKCastle;
    }

    public boolean isBlackQCastle() {
        return blackQCastle;
    }

    public boolean isBlackKCastle() {
        return blackKCastle;
    }

    public BoardCoordinate getEnPassantCoordinates() {
        return enPassantCoordinates;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public int getFullmoveClock() {
        return fullmoveClock;
    }

    /**
     * BoardCoordinate coordinates: The coordinates you need the piece of
     * Returns the piece at the given coordinates. If there is no piece at this coordinate, it returns null
     * @param coordinates The coordinates to get the piece at
     * @return The piece at the given position, null if there is none
     */
    public Piece getPieceAtCoordinates(BoardCoordinate coordinates) {
        for (Piece piece : pieces) {
            if (piece.getCoordinates().equals(coordinates)) {
                return piece;
            }
        }
        return null;
    }
}
