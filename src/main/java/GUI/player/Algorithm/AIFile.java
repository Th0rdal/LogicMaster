package GUI.player.Algorithm;

public class AIFile {
    private final String path;
    private final String name;

    public AIFile(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
