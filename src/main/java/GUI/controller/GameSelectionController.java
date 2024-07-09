package GUI.controller;

import GUI.UIElements.ChessGameCell;
import GUI.handler.GameHandler;
import GUI.handler.SceneHandler;
import database.ChessGame;
import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import java.sql.SQLException;
import java.util.Optional;

public class GameSelectionController {

    @FXML
    private Button backButton;
    @FXML
    private ListView<ChessGame> gameList;

    private GameHandler gameHandler;

    public GameSelectionController(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public void loadElements() {
        this.backButton.setOnAction(e -> {
            SceneHandler.getInstance().activate("index");
        });

        this.gameList.setCellFactory(listView -> new ChessGameCell());

        this.gameList.getItems().clear();
        try {
            this.gameList.getItems().addAll(Database.getInstance().getDao().queryForAll());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.gameList.setOnMouseClicked(event -> {
            ChessGame game = this.gameList.getSelectionModel().getSelectedItem();
            if (gameHandler.isGameInitialized()) {
                //TODO make alerthandler
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("old game found");
                alert.setHeaderText(null);
                alert.setContentText("You already have a game running. Do you wish to continue or start a new Game? The old game will be lost if it was not saved in the database");
                ButtonType buttonYes = new ButtonType("start new game");
                ButtonType buttonNo = new ButtonType("continue old game");
                alert.getButtonTypes().setAll(buttonYes, buttonNo);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == buttonYes) {
                    gameHandler.setShutdownFlag();
                    gameHandler.resetInterruptFlag(true);
                    gameHandler.waitForOldThreadShutdown();
                    new Thread(() -> {
                        gameHandler.loadFromDatabaseAndStartGame(game);
                    }).start();
                } else if (result.isPresent() && result.get() == buttonNo) {
                    gameHandler.resetInterruptFlag(true);
                }
            } else {
                new Thread(() -> {
                    gameHandler.loadFromDatabaseAndStartGame(game);
                }).start();
            }
        });
    }
}
