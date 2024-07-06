package GUI;

import GUI.handler.GameHandler;
import GUI.handler.SceneHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.h2.bnf.Sentence;

public class Main extends Application{

    public static void main(String[] args) {
        System.out.println("Hello world!");
        launch();
    }

    @Override
    public void start(Stage stage) {
        try {
            GameHandler gameHandler = new GameHandler();
            SceneHandler sceneHandler = SceneHandler.createInstance(stage);

            // load board fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/board.fxml"));
            gameHandler.setController(new Controller(stage));
            loader.setController(gameHandler.getController());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            sceneHandler.addScrene("board", scene);

            loader = new FXMLLoader(getClass().getResource("/fxml/index.fxml"));
            root = loader.load();
            scene = new Scene(root);
            sceneHandler.addScrene("index", scene);

            sceneHandler.activate("index");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


