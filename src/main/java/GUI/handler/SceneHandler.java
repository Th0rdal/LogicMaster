package GUI.handler;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class SceneHandler {
    private HashMap<String, Scene> screenMap = new HashMap<>();
    private Stage stage;
    private static SceneHandler instance;

    private SceneHandler(Stage stage) {
        this.stage = stage;
    }

    public static SceneHandler getInstance() {
        return SceneHandler.instance;
    }

    public static SceneHandler createInstance(Stage stage) {
        if (instance == null) {
            SceneHandler.instance = new SceneHandler(stage);
        }
        return SceneHandler.instance;
    }
    public void addScrene(String name, Scene scene) {
        screenMap.put(name, scene);
    }

    public void removeScene(String name) {
        screenMap.remove(name);
    }

    public void activate(String name) {
        this.stage.setScene(this.screenMap.get(name));
        this.stage.show();
    }
}
