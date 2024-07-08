package GUI.player;

import GUI.game.gamestate.Gamestate;
import GUI.player.Algorithm.AlgorithmHandlerBase;
import GUI.utilities.BoardConverter;
import GUI.game.move.Move;

import java.lang.reflect.InvocationTargetException;
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
    private final AlgorithmHandlerBase algorithmHandler;
    private final String name;
    private final String pathToExecutable;

    public Player(boolean isPlayer, String pathToExecutable, String name) {
        this.isHuman = isPlayer;
        String tempPath = isPlayer && pathToExecutable.isEmpty() ? DEFAULTALGORITHMPATH : pathToExecutable;
        this.pathToExecutable = tempPath;
        this.name = name;

        try {
            Class<?> clazz = Class.forName(this.name);
            Object instance = clazz.getDeclaredConstructor(String.class).newInstance(tempPath);
            this.algorithmHandler = (AlgorithmHandlerBase) instance;
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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

    public String getPath() {return this.pathToExecutable;}

}
