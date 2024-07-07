package GUI.player.Algorithm;

import GUI.game.move.Move;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AlgorithmHandlerBase {

    public abstract Move calculateMove(String fen);
    public abstract HashMap<String, ArrayList<Move>> calculatePossibleMoves(String fen);
    public abstract void setParameter();

}
