package GUI;

import GUI.controller.BoardController;
import GUI.controller.GameSelectionController;
import GUI.controller.IndexController;
import GUI.handler.GameHandler;
import GUI.handler.SceneHandler;
import GUI.game.timecontrol.Timecontrol;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args) {
        System.out.println("Hello world!");
        launch();
    }

    @Override
    public void start(Stage stage) {
        new Timecontrol("4+5/45:2+3/46:4+5").getTimecontrolChanges(45);
        try {
            GameHandler gameHandler = new GameHandler();
            SceneHandler sceneHandler = SceneHandler.createInstance(stage);
            BoardController boardController = new BoardController();

            // load board fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/board.fxml"));
            gameHandler.setController(boardController);
            loader.setController(gameHandler.getController());
            Parent root = loader.load();
            Scene boardScene = new Scene(root);
            sceneHandler.addScrene("board", boardScene);
            boardController.loadElements();

            loader = new FXMLLoader(getClass().getResource("/fxml/gameSelection.fxml"));
            GameSelectionController gameSelectionController = new GameSelectionController(gameHandler);
            loader.setController(gameSelectionController);
            root = loader.load();
            Scene databaseScene = new Scene(root);
            sceneHandler.addScrene("gameSelection", databaseScene);
            gameSelectionController.loadElements();

            loader = new FXMLLoader(getClass().getResource("/fxml/index.fxml"));
            IndexController controller = new IndexController();
            loader.setController(controller);
            root = loader.load();
            Scene indexScene = new Scene(root);
            sceneHandler.addScrene("index", indexScene);
            controller.loadElements(gameHandler);

            stage.sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (stage.getScene() == databaseScene) {
                    gameSelectionController.loadElements();
                }
            });

            sceneHandler.activate("index");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


