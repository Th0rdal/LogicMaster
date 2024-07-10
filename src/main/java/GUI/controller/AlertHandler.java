package GUI.controller;

import GUI.exceptions.ProblemWhileWaitingException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AlertHandler {
    // all alert functions define alert twice. once in Platfor.runLater and once without. Alert can only be defined in javafx thread


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
                AlertHandler.throwError();
                throw new ProblemWhileWaitingException("Something went wrong while waiting for a future", e);
            }
        }
    }

    public static void showAlert(Alert.AlertType type, String title, String content) {
        AlertHandler.showAlert(type, title, content, false);
    }

    public static void showAlertAndWait(Alert.AlertType type, String title, String content) {
        AlertHandler.showAlert(type, title, content, true);
    }

    public static boolean showChoiceAlertYesNo(Alert.AlertType type, String title, String content) {
        CompletableFuture<Optional<ButtonType>> future = new CompletableFuture<>();

        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);

            ButtonType buttonYes = new ButtonType("YES");
            ButtonType buttonNo = new ButtonType("NO");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(buttonYes, buttonNo);

            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == buttonYes;
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(type);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(content);

                ButtonType buttonYes = new ButtonType("YES");
                ButtonType buttonNo = new ButtonType("NO");
                alert.getButtonTypes().clear();
                alert.getButtonTypes().addAll(buttonYes, buttonNo);

                Optional<ButtonType> result = alert.showAndWait();
                future.complete(result);
            });
            try {
                return future.get().isPresent() && Objects.equals(future.get().get().getText(), "YES");
            } catch (ExecutionException | InterruptedException e) {
                AlertHandler.throwError();
                throw new ProblemWhileWaitingException("Something went wrong while waiting for a future", e);
            }
        }
    }

    public static boolean showConfirmationAlertAndWait(String title, String content) {
        CompletableFuture<Optional<ButtonType>> future = new CompletableFuture<>();

        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);

            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(content);

                Optional<ButtonType> result = alert.showAndWait();
                future.complete(result);
            });
            try {
                return future.get().isPresent() && future.get().get() == ButtonType.OK;
            } catch (ExecutionException | InterruptedException e) {
                AlertHandler.throwError();
                throw new ProblemWhileWaitingException("Something went wrong while waiting for a future", e);
            }
        }
    }

    public static String showCustomConfirmationAlertAndWait(String title, String content, ArrayList<String> buttonList) {
        CompletableFuture<Optional<ButtonType>> future = new CompletableFuture<>();

        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.getButtonTypes().clear();

            for (String button : buttonList) {
                alert.getButtonTypes().add(new ButtonType(button));
            }
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                return result.get().getText();
            }
            return "";
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(content);
                alert.getButtonTypes().clear();

                for (String button : buttonList) {
                    alert.getButtonTypes().add(new ButtonType(button));
                }
                Optional<ButtonType> result = alert.showAndWait();
                future.complete(result);
            });
            try {
                if (future.get().isPresent()) {
                    return future.get().get().getText();
                }
                return "";
            } catch (ExecutionException | InterruptedException e) {
                AlertHandler.throwError();
                throw new ProblemWhileWaitingException("Something went wrong while waiting for a future", e);
            }
        }
    }

    public static void throwError() {
        CompletableFuture<Optional<ButtonType>> future = new CompletableFuture<>();

        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The program encountered an error it can not recover from. For further information, please look at the stack trace in the console! Shutting down...");

            alert.showAndWait();
            future.complete(Optional.empty());
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The program encountered an error it can not recover from. For further information, please look at the stack trace in the console! Shutting down...");

                alert.showAndWait();
                future.complete(Optional.empty());
            });
        }
        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            AlertHandler.throwError();
            throw new ProblemWhileWaitingException("Something went wrong while waiting for a future", e);
        }
    }

    public static void throwWarningAndWait(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        CompletableFuture<Optional<ButtonType>> future = new CompletableFuture<>();

        if (Platform.isFxApplicationThread()) {
            alert.showAndWait();
        } else {
            Platform.runLater(() -> {
                alert.showAndWait();
                future.complete(Optional.empty());
            });
        }

        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            AlertHandler.throwError();
            throw new ProblemWhileWaitingException("Something went wrong while waiting for a future", e);
        }
    }
}
