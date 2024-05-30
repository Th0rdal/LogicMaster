package GUI;

import GUI.piece.*;
import GUI.utilities.BoardCoordinate;
import GUI.utilities.Move;

import java.util.ArrayList;

public class Board {

    private ArrayList<Piece> pieces = new ArrayList<>();
    private boolean whiteTurn; // true if it is the white turn
    // true if castle is possible
    private boolean whiteQCastle = true, whiteKCastle = true, blackQCastle = true, blackKCastle = true;
    private BoardCoordinate enPassantCoordinates;
    private int halfmoveClock = 0, fullmoveClock = 0;

    private static final String startPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    // should take Move as parameter because move is chosen from Move list
    public void makeMove(BoardCoordinate startCoordinates, BoardCoordinate endCoordinate) {
        Piece piece = this.getPieceAtCoordinates(startCoordinates);
        if (piece == null) {
            //TODO make this a useful Error
            throw new RuntimeException("UNEXPECTED. THIS SHOULD NEVER HAPPEN. piece is null in makeMove");
        }
        piece.makeMove(endCoordinate);
        this.whiteTurn = !this.whiteTurn;
    }

    public boolean isUsablePiece(BoardCoordinate startCoordinates) {
        /*
         * This function checks if the piece at the given coordinates is usable. This means that the piece belongs
         * to the color whose turn it currently is
         */
        Piece piece = this.getPieceAtCoordinates(startCoordinates);
        if (piece == null) { // return false if there is no piece at this coordinates
            return false;
        }

        if (piece.isWhite() != this.whiteTurn) {
            return false;
        }

        return true;

    }

    public void loadStartPosition() {

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

        this.whiteTurn = true;
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
    public Piece getPieceAtCoordinates(BoardCoordinate coordinates) {
        /*
         * BoardCoordinate coordinates: The coordinates you need the piece of
         * Returns the piece at the given coordinates. If there is no piece at this coordinate, it returns null
         */
        for (Piece piece : pieces) {
            if (piece.getCoordinates().equals(coordinates)) {
                return piece;
            }
        }
        return null;
    }
}
