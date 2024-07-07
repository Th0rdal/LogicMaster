package GUI.player;

import GUI.player.Algorithm.AlgorithmHandler;
import GUI.game.gamestate.Gamestate;
import GUI.utilities.BoardConverter;
import GUI.game.move.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

/**
 * This class saves all player information as well as AI information if it is an AI player.
 * It will use FEN notation as default, but if a different algorithm with different requirements is used,
 * create a new class extending Player and overwrite the executeAlgorithm function.
 */
public class Player {

    private static final String DEFAULTALGORITHMPATH = "algorithms/algorithm.exe";
    private final boolean isHuman;
    private final AlgorithmHandler algorithmHandler;
    private final String name;

    public Player(boolean isPlayer, String pathToExecutable, String name) {
        this.isHuman = isPlayer;
        String tempPath = isPlayer && pathToExecutable.isEmpty() ? DEFAULTALGORITHMPATH : pathToExecutable;
        this.name = name;

        this.algorithmHandler = new AlgorithmHandler(tempPath);
        if (!isPlayer) {
            this.algorithmHandler.setParameter();

        }
    }

    public String getName() {
        return name;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public void executeAlgorithm(Gamestate gamestate, boolean whiteTurn, BlockingQueue<Move> moveQueue) throws InterruptedException {
        String fen = BoardConverter.createFEN(gamestate, whiteTurn);
        moveQueue.put(this.algorithmHandler.calculateMove(fen));
    }

    public HashMap<String, ArrayList<Move>> executeGetPossibleMoves(Gamestate gamestate, boolean whiteTurn) {
        String fen = BoardConverter.createFEN(gamestate, whiteTurn);
        return this.algorithmHandler.calculatePossibleMoves(fen);
    }

}
