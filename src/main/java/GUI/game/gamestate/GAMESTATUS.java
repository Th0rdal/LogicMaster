package GUI.game.gamestate;

import java.util.HashMap;

public enum GAMESTATUS {
    ONGOING, CHECKMATE_WHITE, CHECKMATE_BLACK, CHECKMATE_TIME_WHITE, CHECKMATE_TIME_BLACK, DRAW_AGREED, STALEMATE;

    private static final HashMap<GAMESTATUS, String> toStringMap = new HashMap<>();

    static {
        toStringMap.put(ONGOING, "ONGOING");
        toStringMap.put(CHECKMATE_WHITE, "CHECKMATE_WHITE");
        toStringMap.put(CHECKMATE_BLACK, "CHECKMATE_BLACK");
        toStringMap.put(CHECKMATE_TIME_WHITE, "CHECKMATE_TIME_WHITE");
        toStringMap.put(CHECKMATE_TIME_BLACK, "CHECKMATE_TIME_BLACK");
        toStringMap.put(DRAW_AGREED, "DRAW_AGREED");
        toStringMap.put(STALEMATE, "STALEMATE");
    }

    public static String toString(GAMESTATUS status) {
        return toStringMap.get(status);
    }
}
