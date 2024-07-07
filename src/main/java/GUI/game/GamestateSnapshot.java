package GUI.game;

import GUI.piece.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents a "snapshot" of the current configuration of a Gamestate instance
 */
public class GamestateSnapshot {

    private final ArrayList<Piece> pieces;

    // flags
    private final boolean whiteQCastle, whiteKCastle, blackQCastle, blackKCastle;

    private final BoardCoordinate enPassantCoordinates;
    private final int fullmoveCounter, halfmoveCounter;

    private final int clockWhitePlayer, clockBlackPlayer;

    private final Move move;

    /**
     * Create GamestateSnapshot
     * @param pieces
     * @param whiteQCastle
     * @param whiteKCastle
     * @param blackQCastle
     * @param blackKCastle
     * @param enPassantCoordinates
     * @param fullmoveCounter
     * @param halfmoveCounter
     * @param clockWhitePlayer
     * @param clockBlackPlayer
     * @param move
     */
    public GamestateSnapshot(ArrayList<Piece> pieces,
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
        return this.fullmoveCounter%2 == 1;
    }

    public Move getMove() {
        return move;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
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

}
