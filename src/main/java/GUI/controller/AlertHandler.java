package GUI.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AlertHandler {

    private static void showAlert(Alert.AlertType type, String title, String content, boolean wait) {
        CompletableFuture<Optional<ButtonType>> future = new CompletableFuture<>();
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            Platform.runLater(alert::showAndWait);
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(type);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(content);
                Optional<ButtonType> result = alert.showAndWait();
                future.complete(result);
            });
        }

        if (wait) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void showAlert(Alert.AlertType type, String title, String content) {
        AlertHandler.showAlert(type, title, content, false);
    }

    public static void showAlertAndWait(Alert.AlertType type, String title, String content) {
        AlertHandler.showAlert(type, title, content, true);
    }

    public static Optional<ButtonType> showChoiceAlert(Alert.AlertType type, String title, String content, ArrayList<ButtonType> buttons) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(buttons);
        CompletableFuture<Optional<ButtonType>> future = new CompletableFuture<>();

        Platform.runLater(() -> {
            Optional<ButtonType> result = alert.showAndWait();
            future.complete(result);
        });
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean showChoiceAlertYesNo(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType buttonYes = new ButtonType("YES");
        ButtonType buttonNo = new ButtonType("NO");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(buttonYes, buttonNo);

        CompletableFuture<Optional<ButtonType>> future = new CompletableFuture<>();

        if (Platform.isFxApplicationThread()) {
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == buttonYes;
        } else {
            Platform.runLater(() -> {
                Optional<ButtonType> result = alert.showAndWait();
                future.complete(result);
            });
            try {
                return future.get().isPresent() && Objects.equals(future.get().get().getText(), "YES");
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
