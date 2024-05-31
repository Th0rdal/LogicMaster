package GUI.utilities;

import GUI.Board;
import GUI.piece.*;

import java.util.ArrayList;

public class boardConverter {

    /**
     * loads FEN notation and converts it into a board
     * @param board: The board to save to
     * @param fen FEN notation to convert
     */
    public static void loadFEN(Board board, String fen) {
        int rowCounter = 8;
        int colCounter = 1;
        int counter = 0;
        boolean whiteTurn = false;
        boolean whiteQCastle = false, whiteKCastle = false, blackQCastle = false, blackKCastle = false;
        StringBuilder en_passant = new StringBuilder();
        int halfmoveClock = 0, fullmoveNumber = 0;
        ArrayList<Piece> pieces = new ArrayList<>();
        for (char c : fen.toCharArray()) {
            if (c == ' ') {
                counter++;
                continue;
            }
            switch (counter) {
                case 1:
                    whiteTurn = c == 'w';
                    continue;
                case 2:
                    if (c == 'K') {
                        whiteKCastle = true;
                    } else if (c == 'Q') {
                        whiteQCastle = true;
                    } else if (c == 'k') {
                        blackKCastle = true;
                    } else if (c == 'q') {
                        blackQCastle = true;
                    }
                    continue;
                case 3:
                    if (c == '-') {
                        continue;
                    }
                    en_passant.append(c);
                    continue;
                case 4:
                    halfmoveClock = halfmoveClock * 10 + Character.getNumericValue(c);
                    continue;
                case 5:
                    fullmoveNumber = fullmoveNumber * 10 + Character.getNumericValue(c);
                    continue;
            }
            if (Character.isLetter(c)) {
                if (Character.isUpperCase(c)) {
                    pieces.add(boardConverter.getPiece(Character.toLowerCase(c), true, rowCounter, colCounter));
                } else {
                    pieces.add(boardConverter.getPiece(c, false, rowCounter, colCounter));
                }
            } else if (Character.isDigit(c)) {
                colCounter += Character.getNumericValue(c);
            } else if (c == '/') {
                rowCounter--;
                colCounter=1;
            }
        }
        board.loadConfiguration(
                pieces,
                whiteTurn,
                whiteKCastle,
                whiteQCastle,
                blackKCastle,
                blackQCastle,
                en_passant.toString(),
                halfmoveClock,
                fullmoveNumber);
    }

    /**
     * Converts piece abbreviation, color and coordinates into a Piece
     * @param piece: piece abbreviation
     * @param isWhite: true if piece is white, else black
     * @param rowCounter: row position of the piece
     * @param colCounter: counter position of the piece
     * @return Piece subclass corresponding to the char piece
     */
    private static Piece getPiece(char piece, boolean isWhite,  int rowCounter, int colCounter) {
        switch (piece) {
            case 'p':
                return new Pawn(new BoardCoordinate(colCounter, rowCounter), isWhite);
            case 'b':
                return new Bishop(new BoardCoordinate(colCounter, rowCounter), isWhite);
            case 'n':
                return new Knight(new BoardCoordinate(colCounter, rowCounter), isWhite);
            case 'k':
                return new King(new BoardCoordinate(colCounter, rowCounter), isWhite);
            case 'q':
                return new Queen(new BoardCoordinate(colCounter, rowCounter), isWhite);
            case 'r':
                return new Rook(new BoardCoordinate(colCounter, rowCounter), isWhite);
        }
        String message = "piece value is unexpected (" + piece + ")";
        //TODO exchange with useful message and Error
        throw new RuntimeException(message);
    }
}
