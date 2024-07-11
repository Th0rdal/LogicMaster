package GUI.UIElements;

import GUI.Config;
import GUI.controller.AlertHandler;
import GUI.exceptions.DatabaseConnectionException;
import GUI.game.gamestate.Gamestate;
import GUI.piece.Piece;
import GUI.utilities.BoardConverter;
import database.ChessGame;
import database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single row element in the list for games to load
 */
public class ChessGameCell extends ListCell<ChessGame> {
    private static final Paint WON = Color.GREEN;
    private static final Paint LOST = Color.RED;

    @FXML
    private AnchorPane listElementPane;
    @FXML
    private Label whitePlayerName;
    @FXML
    private Label blackPlayerName;
    @FXML
    private Label timeControl;
    @FXML
    private Label gamestatus;
    @FXML
    private Label creationDatetime;
    @FXML
    private GridPane endBoardState;
    @FXML
    private Label turnLabel;
    @FXML
    private Button deleteButton;

    /**
     * updates/fills the row with data based on the ChessGame given
     * @param item: the ChessGame it should represent
     * @param empty: if the row should be empty
     */
    @Override
    protected void updateItem(ChessGame item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listElementView.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (IOException e) {
                AlertHandler.throwWarningAndWait("failed to load fxml file", "While creating an item the loading of the fxml file failed. The element wont be shown");
            }

            switch (item.getGameStatus()) {
                case ONGOING -> this.gamestatus.setText("ONGOING");
                case CHECKMATE_WHITE -> {
                    this.gamestatus.setText("1 : 0 CHECKMATE");
                    this.whitePlayerName.setTextFill(ChessGameCell.WON);
                    this.blackPlayerName.setTextFill(ChessGameCell.LOST);
                }
                case CHECKMATE_BLACK -> {
                    this.gamestatus.setText("0 : 1 CHECKMATE");
                    this.whitePlayerName.setTextFill(ChessGameCell.LOST);
                    this.blackPlayerName.setTextFill(ChessGameCell.WON);
                }
                case DRAW -> this.gamestatus.setText("1/2 : 1/2 AGREED DRAW");
                case STALEMATE -> this.gamestatus.setText("1/2 : 1/2 STALEMATE");
                case CHECKMATE_TIME_WHITE -> {
                    this.gamestatus.setText("1 : 0 LOSS ON TIME");
                    this.whitePlayerName.setTextFill(ChessGameCell.WON);
                    this.blackPlayerName.setTextFill(ChessGameCell.LOST);
                }
                case CHECKMATE_TIME_BLACK -> {
                    this.gamestatus.setText("0 : 1 LOSS ON TIME");
                    this.whitePlayerName.setTextFill(ChessGameCell.LOST);
                    this.blackPlayerName.setTextFill(ChessGameCell.WON);
                }
            }

            this.gamestatus.setMouseTransparent(true);
            this.whitePlayerName.setMouseTransparent(true);
            this.blackPlayerName.setMouseTransparent(true);
            this.timeControl.setMouseTransparent(true);
            this.turnLabel.setMouseTransparent(true);
            this.creationDatetime.setMouseTransparent(true);
            this.endBoardState.setMouseTransparent(true);

            this.whitePlayerName.setText(item.getWhitePlayerName());
            this.blackPlayerName.setText(item.getBlackPlayerName());
            this.timeControl.setText("time control: " + item.getTimeControl());
            this.turnLabel.setText(item.isWhiteTurn() ? "white" : "black" + "'s turn");

            LocalDateTime localDateTime = item.getCreationDatetime().toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
            this.creationDatetime.setText("game date: " + localDateTime.format(formatter));

            Gamestate gamestate = BoardConverter.loadFEN(item.getEndFen());
            if (gamestate != null) {
                int size = 8;
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        StackPane square = new StackPane();
                        square.setMouseTransparent(true);
                        this.endBoardState.add(square, row, col, 1, 1);
                        Color color = (row + col) % 2 == 0 ? Config.squareColorWhite : Config.squareColorBlack;
                        square.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                }
                for (Piece piece : gamestate.getPieces()) {
                    Pane piecePane = piece.getPieceImage(20);
                    piecePane.setMouseTransparent(true);

                    this.endBoardState.add(
                            piecePane,
                            piece.getLocationX()-1,
                            8 - piece.getLocationY());
                }
            }

            this.deleteButton.setOnAction(event -> {
                boolean result = AlertHandler.showConfirmationAlertAndWait("delete saved game", "Do you really want to delete the game from the database? This interaction cannot be reversed.");
                if (result) {
                    try {
                        Database.getInstance().getDao().delete(item);
                    } catch (SQLException e) {
                        AlertHandler.throwError();
                        throw new DatabaseConnectionException("There was an error when trying to delete an item from the view list", e);
                    }
                    getListView().getItems().remove(item);
                }
            });

            if (getIndex() % 2 == 0) {
                setStyle("-fx-background-color: lightgray");
            } else {
                setStyle("-fx-background-color: white");
            }
            setGraphic(listElementPane);
        } else {
            setGraphic(null);
        }
    }
}
