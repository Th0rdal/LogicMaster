package GUI;

import javafx.stage.Stage;

public class Controller {

    private Stage stage;
    private static Controller controller = null;

    private Controller(Stage stage) {
        this.stage = stage;
    }

    public static Controller getController(Stage stage) {
        if (Controller.controller == null) {
            Controller.controller = new Controller(stage);
        }
        return Controller.controller;
    }
}
