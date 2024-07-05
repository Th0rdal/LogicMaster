package GUI;

import GUI.Player.Player;
import GUI.piece.*;
import GUI.utilities.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Represents the chess board (not visual) and handles all calculations.
 */
public class Gamestate {

    private ArrayList<Piece> pieces = new ArrayList<>(); // contains all pieces currently on the board

    // SPECIAL MOVES AND COUNTER VARIABLES

    // true if castle is possible (TRUE IF GENERAL CASTLING IS POSSIBLE. DOES NOT LOOK AT THE BOARD AT ALL)
    private boolean whiteQCastle = true, whiteKCastle = true, blackQCastle = true, blackKCastle = true;
    private BoardCoordinate enPassantCoordinates; // the coordinates of a field that can be taken by en Passant
    private int halfmoveClock = 0; // half move counts moves since last pawn capture or pawn move
    private int fullmoveClock = 0; // full move clock counts the total amount of moves

    private Piece promotionPiece = null;

    /**
     * This function handles everything that is needed to be done, when a move was made.
     * TODO should take Move as parameter because move is chosen from Move list
     * @param move: The move taken
     */
    public GamestateSnapshot makeMove(Move move, boolean whiteTurn, int whiteClockCounter, int blackClockCounter) {
        Piece piece = this.getPieceAtCoordinates(move.getOldPosition());
        if (piece == null) {
            //TODO make this a useful Error
            throw new RuntimeException("UNEXPECTED. THIS SHOULD NEVER HAPPEN. piece is null in makeMove");
        }
        if (move.isCapture()) { // remove piece if already at end position
            this.removePiece(move.getNewPosition());
        } else if (move.getSpecialMove() == SPECIAL_MOVE.KING_CASTLE) {
            piece.makeMove(move.getNewPosition());
            if (whiteTurn) {
                this.getPieceAtCoordinates(new BoardCoordinate("H1")).makeMove(new BoardCoordinate("F1"));
            } else {
                this.getPieceAtCoordinates(new BoardCoordinate("H8")).makeMove(new BoardCoordinate("F8"));
            }
        } else if (move.getSpecialMove() == SPECIAL_MOVE.QUEEN_CASTLE) {
            if (whiteTurn) {
                this.getPieceAtCoordinates(new BoardCoordinate("A1")).makeMove(new BoardCoordinate("D1"));
            } else {
                this.getPieceAtCoordinates(new BoardCoordinate("A8")).makeMove(new BoardCoordinate("D8"));
            }
        } else if (move.getSpecialMove() == SPECIAL_MOVE.EN_PASSANT) {
            this.removePiece(new BoardCoordinate(move.getNewPosition().getXLocation(), move.getNewPosition().getYLocation()-1));
        }

        piece.makeMove(move.getNewPosition());

        this.fullmoveClock++;
        if (piece.getID() == PIECE_ID.PAWN || move.isCapture()) {
            this.halfmoveClock = 0;
        } else {
            this.halfmoveClock++;
        }

        if (move.getSpecialMove() == SPECIAL_MOVE.PROMOTION) { // temp promotion code later removed for moveList
                this.removePiece(move.getNewPosition());
                pieces.add(promotionPiece);
        }

        return this.saveSnapshot(move, whiteClockCounter, blackClockCounter);
    }

    public Piece promotePawn(Piece piece, PIECE_ID promoteTo) {
        if (piece.getID() != PIECE_ID.PAWN) {
            // TODO change
            throw new RuntimeException("trying to promote something that is not a pawn");
        }
        this.promotionPiece = ((Pawn) piece).promote(promoteTo);
        return this.promotionPiece;
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
    public boolean isUsablePiece(BoardCoordinate coordinates, boolean whiteTurn) {
        Piece piece = this.getPieceAtCoordinates(coordinates);
        if (piece == null) { // return false if there is no piece at this coordinates
            return false;
        }

        return piece.isWhite() == whiteTurn;

    }

    public void clearBoard() {
        this.pieces = new ArrayList<>();
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

        // all Castling possible
        this.whiteQCastle = true;
        this.whiteKCastle = true;
        this.blackQCastle = true;
        this.blackKCastle = true;
        this.enPassantCoordinates = null;
        this.halfmoveClock = 0;
        this.fullmoveClock = 0;
        this.enPassantCoordinates = new BoardCoordinate("-");

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

    private GamestateSnapshot saveSnapshot(Move move, int whiteClockCounter, int blackClockCounter) {
        return new GamestateSnapshot(
                this.pieces,
                this.whiteQCastle,
                this.whiteKCastle,
                this.blackQCastle,
                this.blackKCastle,
                this.enPassantCoordinates,
                this.fullmoveClock,
                this.halfmoveClock,
                whiteClockCounter,
                blackClockCounter,
                move);
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



    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public boolean canWhiteQCastle() {
        return whiteQCastle;
    }

    public boolean canWhiteKCastle() {
        return whiteKCastle;
    }

    public boolean canBlackQCastle() {
        return blackQCastle;
    }

    public boolean canBlackKCastle() {
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
