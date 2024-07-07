package GUI.Controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertHandler {

    public static void throwAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
