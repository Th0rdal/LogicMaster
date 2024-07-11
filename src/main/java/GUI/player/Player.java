package GUI.player;

import GUI.controller.AlertHandler;
import GUI.exceptions.AlgorithmLoadingException;
import GUI.game.gamestate.Gamestate;
import GUI.player.algorithm.AlgorithmHandlerBase;
import GUI.player.algorithm.BadMinimax;
import GUI.utilities.BoardConverter;
import GUI.game.move.Move;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

/**
 * This class saves all player information as well as AI information if it is an AI player.
 * It will use FEN notation as default, but if a different algorithm with different requirements is used,
 */
public class Player {

    private static final String DEFAULTNAME = "BadMinimax";
    private static final String DEFAULTALGORITHMPATH = "algorithms/" + DEFAULTNAME;
    private static final String DEFAULTNAMEPREFIX = "GUI.player.algorithm.";
    private final boolean isHuman;
    private final AlgorithmHandlerBase algorithmHandler;
    private final String name;
    private final String pathToExecutable;

    /**
     * constructor to create a new player. It will dynamically load the needed AlgorithmHandlerBase extending class.
     * @param isPlayer: true if the player is human
     * @param pathToExecutable: the path to the algorithm used ("" if the player is human)
     * @param name: the name of the player/algorithm
     */
    public Player(boolean isPlayer, String pathToExecutable, String name) {
        this.isHuman = isPlayer;
        String tempPath = isPlayer && pathToExecutable.isEmpty() ? DEFAULTALGORITHMPATH : pathToExecutable;
        this.pathToExecutable = isPlayer ? "" : pathToExecutable;
        this.name = name;

        try {
            if (!isPlayer) {
                Class<?> clazz = Class.forName(Player.DEFAULTNAMEPREFIX + this.name);
                Object instance = clazz.getDeclaredConstructor(String.class).newInstance(tempPath);
                this.algorithmHandler = (AlgorithmHandlerBase) instance;
                this.algorithmHandler.setParameter();
            } else {
                this.algorithmHandler = (BadMinimax) Class.forName(Player.DEFAULTNAMEPREFIX+Player.DEFAULTNAME).getDeclaredConstructor(String.class).newInstance(DEFAULTALGORITHMPATH);
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            AlertHandler.throwError();
            throw new AlgorithmLoadingException("The algorithm could not be loaded", e);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isHuman() {
        return isHuman;
    }

    /**
     * executes the algorithm to get the best possible move by calling its algorithmHandler
     * @param gamestate: the gamestate to check
     * @param whiteTurn: whose turn it currently is
     * @param moveQueue: the move queue to put the result in after calculation
     * @throws InterruptedException if there was a problem adding the move to the queue
     */
    public void executeAlgorithm(Gamestate gamestate, boolean whiteTurn, BlockingQueue<Move> moveQueue) throws InterruptedException {
        String fen = BoardConverter.createFEN(gamestate, whiteTurn);
        moveQueue.put(this.algorithmHandler.calculateMove(fen));
    }

    /**
     * executes the algorithm to get all possible moves from the given gamestate
     * @param gamestate: the gamestate to use
     * @param whiteTurn: whose turn it currently is
     * @return All possible moves as a Hashmap with a String representation of the starting position as the key and an ArrayList of all possible moves as the value
     */
    public HashMap<String, ArrayList<Move>> executeGetPossibleMoves(Gamestate gamestate, boolean whiteTurn) {
        String fen = BoardConverter.createFEN(gamestate, whiteTurn);
        return this.algorithmHandler.calculatePossibleMoves(fen);
    }

    public String getPath() {return this.pathToExecutable;}

}
