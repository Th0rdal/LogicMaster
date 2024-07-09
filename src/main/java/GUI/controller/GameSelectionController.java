package GUI.controller;

import GUI.UIElements.ChessGameCell;
import GUI.exceptions.DatabaseConnectionException;
import GUI.handler.GameHandler;
import GUI.handler.SceneHandler;
import database.ChessGame;
import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

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
            AlertHandler.throwError();
            throw new DatabaseConnectionException("There was an error when trying to delete an item from the view list", e);
        }

        this.gameList.setOnMouseClicked(event -> {
            ChessGame game = this.gameList.getSelectionModel().getSelectedItem();
            if (gameHandler.isGameInitialized()) {
                String title = "old game found";
                String context = "You already have a game running. Do you wish to continue or start a new Game? The old game will be lost if it was not saved in the database";
                ArrayList<String> temp = new ArrayList<>();
                temp.add("load game");
                temp.add("continue old game");
                if (Objects.equals(AlertHandler.showCustomConfirmationAlertAndWait(title, context, temp), "load game")) {
                    gameHandler.setShutdownFlag();
                    gameHandler.resetInterruptFlag();
                    gameHandler.waitForOldThreadShutdown();
                    new Thread(() -> {
                        gameHandler.loadFromDatabaseAndStartGame(game);
                    }).start();
                } else {
                    gameHandler.resetInterruptFlag();
                }
            } else {
                new Thread(() -> {
                    gameHandler.loadFromDatabaseAndStartGame(game);
                }).start();
            }
        });
    }
}
