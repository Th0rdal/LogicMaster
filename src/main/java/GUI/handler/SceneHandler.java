package GUI.handler;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Handles the scene changes. Designed as a Singleton, but it needs to be created first with createInstance.
 * Scenes are added and then can be called by their string name and activated
 */
public class SceneHandler {
    private HashMap<String, Scene> screenMap = new HashMap<>();
    private Stage stage;
    private static SceneHandler instance;

    /**
     * the main stage to use to swap into
     * @param stage: main stage (mostly given by javafx in the start function)
     */
    private SceneHandler(Stage stage) {
        this.stage = stage;
    }

    /**
     * returns the instance of SceneBuilder
     * @return: scene of the instance
     */
    public static SceneHandler getInstance() {
        return SceneHandler.instance;
    }

    /**
     * creates a new instance of SceneHandler if non exists
     * @param stage: the main stage
     * @return SceneHandler object
     */
    public static SceneHandler createInstance(Stage stage) {
        if (instance == null) {
            SceneHandler.instance = new SceneHandler(stage);
        }
        return SceneHandler.instance;
    }

    /**
     * adds a scene to the sceneHandler.
     * @param name: the reference name of the scene
     * @param scene: the loaded scene
     */
    public void addScrene(String name, Scene scene) {
        screenMap.put(name, scene);
    }

    /**
     * removes a scene from the sceneHandler
     * @param name: the reference name of the scene
     */
    public void removeScene(String name) {
        screenMap.remove(name);
    }

    /**
     * activates a scene by setting it
     * @param name: the reference name of the scene
     */
    public void activate(String name) {
        Platform.runLater(() -> {
            this.stage.setScene(this.screenMap.get(name));
            this.stage.show();
        });
    }
}
