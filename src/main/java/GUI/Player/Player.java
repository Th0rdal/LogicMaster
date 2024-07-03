package GUI.Player;

import GUI.Gamestate;
import GUI.utilities.AlgorithmHandler;
import GUI.utilities.BoardConverter;

import java.util.HashMap;

/**
 * This class saves all player information as well as AI information if it is an AI player.
 * It will use FEN notation as default, but if a different algorithm with different requirements is used,
 * create a new class extending Player and overwrite the executeAlgorithm function.
 */
public class Player {

    private final boolean isHuman;
    private final String pathToExecutable; // should be empty string if the player is a human
    private final AlgorithmHandler algorithmHandler;
    private final String name;

    public Player(boolean isPlayer, String pathToExecutable, String name) {
        this.isHuman = isPlayer;
        this.pathToExecutable = pathToExecutable;
        this.name = name;

        if (!isPlayer) {
            this.algorithmHandler = new AlgorithmHandler(this.pathToExecutable);
            HashMap<String, String> temp = new HashMap<>();
            temp.put("-ifen", "");
            temp.put("-md", "4");
            temp.put("-om", "");
            temp.put("-mt", "16");
            this.algorithmHandler.setParameter(temp);
        } else {
            this.algorithmHandler = null;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public void executeAlgorithm(Gamestate gamestate) {
        String fen = BoardConverter.createFEN(gamestate);
        this.algorithmHandler.executeAlgorithm(fen);
    }
    public AlgorithmHandler getAlgorithmHandler() {
        return algorithmHandler;
    }
}
