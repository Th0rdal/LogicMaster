package GUI.piece;

import java.util.HashMap;

public enum PIECE_ID {
    PAWN, KING, QUEEN, BISHOP, KNIGHT, ROOK;

    private static final HashMap<PIECE_ID, String> toAbbreviationMap = new HashMap<>();
    private static final HashMap<PIECE_ID, String> toFenAbbreviationMap = new HashMap<>();
    static {
        toAbbreviationMap.put(PAWN, "");
        toAbbreviationMap.put(KING, "K");
        toAbbreviationMap.put(QUEEN, "Q");
        toAbbreviationMap.put(BISHOP, "B");
        toAbbreviationMap.put(KNIGHT, "N");
        toAbbreviationMap.put(ROOK, "R");

        toFenAbbreviationMap.put(PAWN, "P");
        toFenAbbreviationMap.put(KING, "K");
        toFenAbbreviationMap.put(QUEEN, "Q");
        toFenAbbreviationMap.put(BISHOP, "B");
        toFenAbbreviationMap.put(KNIGHT, "N");
        toFenAbbreviationMap.put(ROOK, "R");
    }

    public static String toAbbreviation(PIECE_ID pieceId) {
        return toAbbreviationMap.get(pieceId);
    }

    public static String toFenAbbreviation(PIECE_ID pieceId, boolean isWhite) {
        if (isWhite) {
            return toFenAbbreviationMap.get(pieceId);
        } else {
            return toFenAbbreviationMap.get(pieceId).toLowerCase();
        }
    }
}
