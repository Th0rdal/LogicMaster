package GUI.piece;

import java.util.HashMap;

public enum PIECE_ID {
    PAWN, KING, QUEEN, BISHOP, KNIGHT, ROOK;

    private static final HashMap<PIECE_ID, String> toAbbreviationMap = new HashMap<>();
    private static final HashMap<PIECE_ID, String> toFenAbbreviationMap = new HashMap<>();
    private static final HashMap<String, PIECE_ID> toNameMap = new HashMap<>();
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

        toNameMap.put("P", PIECE_ID.PAWN);
        toNameMap.put("R", PIECE_ID.ROOK);
        toNameMap.put("N", PIECE_ID.KNIGHT);
        toNameMap.put("B", PIECE_ID.BISHOP);
        toNameMap.put("Q", PIECE_ID.QUEEN);
        toNameMap.put("K", PIECE_ID.KING);
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

    public static PIECE_ID fromString(String pieceId) {
        return toNameMap.get(pieceId);
    }
}
