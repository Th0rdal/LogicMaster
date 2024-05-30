package GUI.piece;

import java.util.HashMap;

public enum PIECE_ID {
    PAWN, KING, QUEEN, BISHOP, KNIGHT, ROOK;

    private static final HashMap<PIECE_ID, String> toAbbriviationMap = new HashMap<>();

    static {
        toAbbriviationMap.put(PAWN, "");
        toAbbriviationMap.put(KING, "K");
        toAbbriviationMap.put(QUEEN, "Q");
        toAbbriviationMap.put(BISHOP, "B");
        toAbbriviationMap.put(KNIGHT, "N");
        toAbbriviationMap.put(ROOK, "R");
    }

    public static String toAbbreviation(PIECE_ID pieceId) {
        return toAbbriviationMap.get(pieceId);
    }
}
