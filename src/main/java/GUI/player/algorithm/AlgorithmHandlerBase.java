package GUI.player.algorithm;

import GUI.game.move.Move;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * base class for all algorithm classes (one needed for each algorithm used)
 */
public abstract class AlgorithmHandlerBase {

    public abstract Move calculateMove(String fen);
    public abstract HashMap<String, ArrayList<Move>> calculatePossibleMoves(String fen);
    public abstract void setParameter();

}
