package GUI.utilities;

import GUI.game.gamestate.Gamestate;
import GUI.piece.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoardConverter {

    /**
     * loads FEN notation and converts it into a board
     * @param fen FEN notation to convert
     */
    public static Gamestate loadFEN(String fen) {

        if (!BoardConverter.validFEN(fen)) {
            return null;
        }

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
                    // no need to check for -, because it has no extra effect. continue will skip the character and go to part 3
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
                colCounter++;
            } else if (Character.isDigit(c)) {
                colCounter += Character.getNumericValue(c);
            } else if (c == '/') {
                rowCounter--;
                colCounter=1;
            }
        }

        return new Gamestate(
                pieces,
                whiteTurn,
                whiteKCastle,
                whiteQCastle,
                blackKCastle,
                blackQCastle,
                en_passant.toString().isEmpty() ? "-" :  en_passant.toString(), // if en_passant is "" then it is a - in the fen notation
                halfmoveClock,
                fullmoveNumber-1);
    }

    /**
     * checks if the fen notation is valid
     * @param fen: the fen notation to check
     * @return: true if it is valid, else false
     */
    public static boolean validFEN(String fen) {
        Pattern fenPattern = Pattern.compile("^(([pnbrqkPNBRQK1-8]{1,8})\\/?){8}\\s+(b|w)\\s+(-|K?Q?k?q)\\s+(-|[a-h][3-6])\\s+(\\d+)\\s+(\\d+)\\s*");
        Matcher matcher = fenPattern.matcher(fen.strip());
        return matcher.matches();
    }

    /**
     * creates a fen out of the gamestate
     * @param gamestate: the gamestate to create fen from
     * @param whiteTurn: whose turn it currently is
     * @return: the fen notation string
     */
    public static String createFEN(Gamestate gamestate, boolean whiteTurn) {
        StringBuilder fen = new StringBuilder();
        String[][] piecesChars = new String[8][8];

        for (Piece piece : gamestate.getPieces()) {
            piecesChars[piece.getLocationY()-1][piece.getLocationX()-1] = PIECE_ID.toFenAbbreviation(piece.getID(), piece.isWhite());
        }

        int counter = 0;
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                if (piecesChars[i][j] == null) {
                    counter++;
                } else {
                    if (counter > 0) {
                        fen.append(counter);
                        counter = 0;
                    }
                    fen.append(piecesChars[i][j]);
                }
            }
            if (counter > 0) {
                fen.append(counter);
                counter = 0;
            }
            fen.append("/");
        }

        fen.append(" ");
        fen.append(whiteTurn? 'w' : 'b');

        fen.append(" ");
        if (gamestate.canWhiteKCastle() || gamestate.canWhiteQCastle() || gamestate.canBlackKCastle() || gamestate.canBlackQCastle()) {
            fen.append(gamestate.canWhiteKCastle() ? 'K' : "");
            fen.append(gamestate.canWhiteQCastle() ? 'Q' : "");
            fen.append(gamestate.canBlackKCastle() ? 'k' : "");
            fen.append(gamestate.canBlackQCastle() ? 'q' : "");
        } else {
            fen.append("-");
        }

        fen.append(" ");
        fen.append(gamestate.getEnPassantCoordinates().toLowerCaseString());

        fen.append(" ");
        fen.append(gamestate.getHalfmoveCounter());

        fen.append(" ");
        fen.append(gamestate.getFullmoveCounter() == 0 ? gamestate.getFullmoveCounter()+1 : gamestate.getFullmoveCounter());

        return fen.toString();
    }
}
