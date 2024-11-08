package GUI.game.gamestate;

import GUI.game.BoardCoordinate;
import GUI.game.move.Move;
import GUI.piece.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents a "snapshot" of the current configuration of a Gamestate instance
 */
public class GamestateSnapshot {

    private final ArrayList<Piece> pieces;

    // flags
    private final boolean whiteQCastle, whiteKCastle, blackQCastle, blackKCastle, whiteTurn;

    private final BoardCoordinate enPassantCoordinates;
    private final int fullmoveCounter, halfmoveCounter;

    private final int clockWhitePlayer, clockBlackPlayer;

    private final Move move;

    /**
     * Create GamestateSnapshot
     * @param pieces: an ArrayList of all pieces in form of piece objects
     * @param whiteKCastle: true if king castling is possible for white, else false
     * @param whiteQCastle: true if queen castling is possible for white, else false
     * @param blackKCastle: true if king castling is possible for black, else false
     * @param blackQCastle: true if queen castling is possible for black, else false
     * @param enPassantCoordinates: A string representing the en-passant position
     * @param fullmoveCounter: the current full move counter
     * @param halfmoveCounter: the current half move counter
     * @param clockWhitePlayer: the current clock counter of white
     * @param clockBlackPlayer: the current clock counter of black
     */
    public GamestateSnapshot(ArrayList<Piece> pieces,
                             boolean whiteTurn,
                             boolean whiteQCastle,
                             boolean whiteKCastle,
                             boolean blackQCastle,
                             boolean blackKCastle,
                             BoardCoordinate enPassantCoordinates,
                             int fullmoveCounter,
                             int halfmoveCounter,
                             int clockWhitePlayer,
                             int clockBlackPlayer,
                             Move tempMove) {

        this.pieces = (ArrayList<Piece>) pieces.stream().map(piece -> {
            switch (piece.getID()) {
                case PAWN -> {
                    return new Pawn((Pawn) piece);
                }
                case ROOK -> {
                    return new Rook((Rook) piece);
                }
                case KNIGHT -> {
                    return new Knight((Knight) piece);
                }
                case BISHOP -> {
                    return new Bishop((Bishop) piece);
                }
                case QUEEN -> {
                    return new Queen((Queen) piece);
                }
                case KING -> {
                    return new King((King) piece);
                }
            }
            return null;
        }).collect(Collectors.toList());
        this.whiteTurn = whiteTurn;
        this.whiteQCastle = whiteQCastle;
        this.whiteKCastle = whiteKCastle;
        this.blackQCastle = blackQCastle;
        this.blackKCastle = blackKCastle;
        this.enPassantCoordinates = new BoardCoordinate(enPassantCoordinates);
        this.fullmoveCounter = fullmoveCounter;
        this.halfmoveCounter = halfmoveCounter;
        this.clockWhitePlayer = clockWhitePlayer;
        this.clockBlackPlayer = clockBlackPlayer;
        if (tempMove == null) {
            this.move = null;
        } else {
            this.move = new Move(tempMove);
        }
    }

    public boolean isWhiteTurn() {
        return this.whiteTurn;
    }

    public Move getMove() {
        return move;
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

    public int getFullmoveCounter() {
        return fullmoveCounter;
    }

    public int getHalfmoveCounter() {
        return halfmoveCounter;
    }

    public int getClockWhitePlayer() {
        return clockWhitePlayer;
    }

    public int getClockBlackPlayer() {
        return clockBlackPlayer;
    }

    public int getClockChanged() {
        if (this.isWhiteTurn()) {
            return this.clockWhitePlayer;
        } else {
            return this.clockBlackPlayer;
        }
    }

    /**
     * returns the current move counter based on the full move counter
     * @return int representing the current move
     */
    public int getMoveCounter() {
        return this.whiteTurn ? this.fullmoveCounter * 2 - 1: this.fullmoveCounter * 2;
    }

}
