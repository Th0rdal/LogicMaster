package GUI;

import GUI.Controller.BoardController;
import GUI.Controller.IndexController;
import GUI.handler.GameHandler;
import GUI.handler.SceneHandler;
import GUI.utilities.Timecontrol;
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

            // load board fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/board.fxml"));
            gameHandler.setController(new BoardController(stage));
            loader.setController(gameHandler.getController());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            sceneHandler.addScrene("board", scene);

            loader = new FXMLLoader(getClass().getResource("/fxml/index.fxml"));
            IndexController controller = new IndexController();
            loader.setController(controller);
            root = loader.load();
            scene = new Scene(root);
            sceneHandler.addScrene("index", scene);
            controller.loadData(gameHandler);

            sceneHandler.activate("index");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


