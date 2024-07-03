package GUI;

public class Player {

    private final boolean isHuman;
    private final String pathToExecutable; // should be empty string if the player is a human

    private final String name;

    public Player(boolean isPlayer, String pathToExecutable, String name) {
        this.isHuman = isPlayer;
        this.pathToExecutable = pathToExecutable;
        this.name = name;
    }

    public String getPathToExecutable() {
        return pathToExecutable;
    }

    public String getName() {
        return name;
    }

    public boolean isHuman() {
        return isHuman;
    }
}
