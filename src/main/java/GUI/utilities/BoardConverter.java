package GUI.utilities;

import GUI.Board;
import GUI.piece.*;

import java.util.ArrayList;

public class BoardConverter {

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
                pieces.add(Piece.createFromChar(c, colCounter, rowCounter));
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

    public static String createFEN(Board board) {
        StringBuilder fen = new StringBuilder();
        String[][] piecesChars = new String[8][8];

        for (Piece piece : board.getPieces()) {
            piecesChars[piece.getLocationX()-1][8-piece.getLocationY()] = PIECE_ID.toFenAbbreviation(piece.getID(), piece.isWhite());
        }

        int counter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (piecesChars[i][j] == null) {
                    counter++;
                } else {
                    fen.append(counter);
                    counter = 0;
                    fen.append(piecesChars[i][j]);
                }
            }
            fen.append("/");
        }

        fen.append(" ");
        fen.append(board.isWhiteTurn() ? 'w' : 'b');

        fen.append(" ");
        fen.append(board.canWhiteKCastle() ? 'K' : "");
        fen.append(board.canWhiteQCastle() ? 'Q' : "");
        fen.append(board.canBlackKCastle() ? 'k' : "");
        fen.append(board.canBlackQCastle() ? 'q' : "");

        fen.append(" ");
        fen.append(board.getEnPassantCoordinates().toLowerCaseString());

        fen.append(" ");
        fen.append(board.getHalfmoveClock());

        fen.append(" ");
        fen.append(board.getFullmoveClock());

        return fen.toString();
    }

    /**
     * Converts a board into a bitboard representation based on the
     * algorithm.
     * TODO write documents
     * @param board The board to create the bitboard from
     * @return bitboard array
     */
    public static long[] toBitboard(Board board) {
        long[] bitboard = new long[9];
        for (int i = 0; i < 9; i++) {
            bitboard[i] = 0;
        }
        int occupancy = 0;
        int pawn = 1, knight = 2, bishop = 3, rook = 4, queen = 5, king = 6;
        int white = 7, black = 8;

        for (Piece piece : board.getPieces()) {
            long mask = 1L << piece.getCoordinates().getLocationInt();
            bitboard[occupancy] |= mask;
            switch (piece.getID()) {
                case PAWN -> bitboard[pawn] |= mask;
                case KNIGHT -> bitboard[knight] |= mask;
                case BISHOP -> bitboard[bishop] |= mask;
                case ROOK -> bitboard[rook] |= mask;
                case QUEEN -> bitboard[queen] |= mask;
                case KING -> bitboard[king] |= mask;
            }
            if (piece.isWhite()) {
                bitboard[white] |= mask;
            } else {
                bitboard[black] |= mask;
            }
        }
        return bitboard;
    }

}
