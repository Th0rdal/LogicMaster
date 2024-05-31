package GUI;

import GUI.piece.*;
import GUI.utilities.BoardCoordinate;
import GUI.utilities.Calculator;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Controller {

    private Stage stage = null;
    private static Controller controller = null;

    @FXML
    private GridPane visualBoard;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label clockOpponentLabel;
    @FXML
    private Label clockPlayerLabel;
    @FXML
    private Label moveHistoryTextField;

    // colors used for the board
    Color color1 = Color.SIENNA;
    Color color2 = Color.LIGHTGRAY;

    Pane selectedPane;  //represents the currently selected pane
    boolean selected = false; //true if a pane is currently selected
    BoardCoordinate startCoordinates;   //saves the start coordinations of the selected pane
    boolean whiteSideDown = true;   //TODO change. just a placeholder for board rotation. if true, white is starting on the bottom
    Board board;


    private Controller(Stage stage) {
        this.stage = stage;
        this.board = new Board();
    }

    public static Controller getController(Stage stage) {
        if (Controller.controller == null) {
            Controller.controller = new Controller(stage);
        }
        return Controller.controller;
    }

    /**
     * Resets the board
     */
    public void clearBoard() {
        this.visualBoard.getChildren().clear();
        this.board.clearBoard();
    }

    /**
     * Clears everything and starts a new board
     */
    public void startNewBoard() {
        this.clearBoard();
        this.board.loadStartPosition();
        this.loadBoard();
    }

    /**
     * creates the board visually and adds all the pieces based on the board variable
     * adds event listeners for piece movement
     */
    public void loadBoard() {
        /*
         * This function loads the board with squares and gives each square a chess board color
         */

        // adding squares to board
        int size = 8;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Square square = new Square(row, col);
                this.visualBoard.add(square, row, col, 1, 1);
                Color color = (row + col) % 2 == 0 ? this.color1 : this.color2;
                square.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

            }
        }

        for (Piece piece : this.board.getPieces()) {
            visualBoard.add(piece.getPieceImage(), piece.getLocationX()-1, whiteSideDown ? 8 - piece.getLocationY() : piece.getLocationY());
        }

        this.board.loadClocks(this.clockOpponentLabel, this.clockPlayerLabel);

        visualBoard.setOnMousePressed(event -> {
            for (Node node : visualBoard.getChildren()) {
                if (node instanceof Pane && !(node instanceof StackPane)) {
                    Pane tempPane = (Pane) node;
                    if (tempPane.getBoundsInParent().contains(event.getX(), event.getY())) { // mouse is in a pane
                        //tempPane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null))); if change of selected pane is needed
                        startCoordinates = new BoardCoordinate((int) Math.floor(event.getX()/100), whiteSideDown ? 8 - (int) Math.floor(event.getY()/100) : (int) Math.floor(event.getY()/100));
                        if (this.board.isUsablePiece(startCoordinates)) {
                            selectedPane = tempPane;
                        } else {
                            startCoordinates = null;
                        }
                    }
                }
            }
        });

        visualBoard.setOnMouseDragged(event -> {
            if (selectedPane == null) {
                return;
            }

            ImageView tempView = (ImageView) selectedPane.getChildren().get(0);

            double tempX = event.getX() - selectedPane.localToParent(0, 0).getX() - (tempView.getFitWidth() / 2);
            double tempY = event.getY() - selectedPane.localToParent(0, 0).getY() - (tempView.getFitHeight() / 2);
            tempView.setLayoutX(tempX);
            tempView.setLayoutY(tempY);
        });

        visualBoard.setOnMouseReleased(event -> {
            if (selectedPane == null) {
                return;
            }

            this.checkMovePossible((int) event.getX(), (int) event.getY());
        });

        visualBoard.setOnMouseClicked(event -> {
            if (selectedPane == null) {
                return;
            }

            if (selected) {
                this.checkMovePossible((int) event.getX(), (int) event.getY());
            }
        });
        stage.show();
    }

    /**
     * handles a made move
     * @param x: x position of the mouse
     * @param y: y position of the mouse
     */
    private void checkMovePossible(int x, int y) {
        ImageView tempView = (ImageView) selectedPane.getChildren().get(0);

        // if this is true, the mouse is outside the board
        if (x < 0 || x > visualBoard.getWidth() || y < 0 || y > visualBoard.getHeight()) {
            tempView.setLayoutX(0);
            tempView.setLayoutY(0);
            return;
        }

        int tempX = (Calculator.round((x), 2) / 100);
        int tempY = (Calculator.round((y), 2) / 100);

        if (tempY >= 8 || tempX >= 8) { // if tempX/Y is 8 or more, it is outside the board
            tempView.setLayoutX(0);
            tempView.setLayoutY(0);
            return;
        }

        BoardCoordinate tempCoordinates = new BoardCoordinate(tempX, whiteSideDown ? 8 - (int) (double) (y / 100) : tempY);
        GridPane.setRowIndex(selectedPane, tempY);
        GridPane.setColumnIndex(selectedPane, tempX);
        tempView.setLayoutX(0);
        tempView.setLayoutY(0);

        if (!Objects.equals(startCoordinates, tempCoordinates)) {
            this.makeMove(tempCoordinates);
        }
    }

    private void makeMove(BoardCoordinate tempCoordinates) {
        selected = false;
        //selectedPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null))); if color change of pane needed
        selectedPane = null;
        board.makeMove(startCoordinates, tempCoordinates);
    }



}
