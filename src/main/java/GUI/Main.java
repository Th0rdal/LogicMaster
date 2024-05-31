package GUI;

import GUI.utilities.boardConverter;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/board.fxml"));
            Controller controller = Controller.getController(stage);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            controller.startNewBoard();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


