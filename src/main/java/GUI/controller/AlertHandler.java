package GUI.controller;

import GUI.exceptions.ProblemWhileWaitingException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AlertHandler {
    // all alert functions define alert twice. once in Platfor.runLater and once without. Alert can only be defined in javafx thread


    /**
     * shows the user an alert with the given title and content. If wait is set, the ui will wait until the user handles the alert
     * @param type: the type of alert
     * @param title: the title of the alert
     * @param content: the body of the alert
     * @param wait: true if the ui should wait for userinput
     */
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

    /**
     * A wrapper function for showAlert, which does not wait for the user to handle the alert
     * @param type: the type of the alert
     * @param title: the title of the alert
     * @param content: the body of the alert
     */
    public static void showAlert(Alert.AlertType type, String title, String content) {
        AlertHandler.showAlert(type, title, content, false);
    }

    /**
     * A wrapper function for showAlert, which makes the ui wait for the user to handle the alert
     * @param type
     * @param title
     * @param content
     */
    public static void showAlertAndWait(Alert.AlertType type, String title, String content) {
        AlertHandler.showAlert(type, title, content, true);
    }

    /**
     * shows a confirmation alert and waits for the user to handle the alert. Returns true or false, depending on the button pressed
     * @param title: the title of the alert
     * @param content: the body of the alert
     * @return: true if the yes button was pressed, otherwise false
     */
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

    /**
     * shows a confirmation alert and waits for the user to handle the alert. The buttons can be given in a String ArrayList.
     * @param title: the title of the alert
     * @param content: the body of the alert
     * @param buttonList: ArrayList of strings with all button names
     * @return: the button string that was pressed
     */
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

    /**
     * notifies the user that an error occurred
     */
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

    /**
     * notifies the user that an error occurs and waits for the user to handle the alert
     * @param title: the title of the alert
     * @param content: the body of the alert
     */
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
