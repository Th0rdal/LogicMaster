package GUI;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Objects;

public class Controller {

    private Stage stage;
    private static Controller controller = null;

    @FXML
    private GridPane board;
    @FXML
    private AnchorPane anchorPane;

    // colors used for the board
    Color color1 = Color.SIENNA;
    Color color2 = Color.LIGHTGRAY;

    Pane selectedPane;  //represents the currently selected pane
    boolean selected = false; //true if a pane is currently selected
    BoardCoordinate startCoordinates;   //saves the start coordinations of the selected pane
    boolean whiteSideDown = true;   //TODO change. just a placeholder for board rotation. if true, white is starting on the bottom
    int[][] boardPiecesArray = new int[8][8];



    private Controller(Stage stage) {
        this.stage = stage;
        for (int[] tempArray : boardPiecesArray) {
            for (int temp : tempArray) {
                temp = 0;
            }
        }
        boardPiecesArray[1][1] = 1;
        boardPiecesArray[2][2] = 1;
    }

    public static Controller getController(Stage stage) {
        if (Controller.controller == null) {
            Controller.controller = new Controller(stage);
        }
        return Controller.controller;
    }



    public void loadBoard() {
        /**
         * This function loads the board with squares and gives each square a chess board color
         */

        // adding squares to board
        int size = 8;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Square square = new Square(row, col);
                this.board.add(square, row, col, 1, 1);
                Color color = (row + col) % 2 == 0 ? this.color1 : this.color2;
                square.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

            }
        }

        // add pieces to board
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (boardPiecesArray[row][col] == 1) {
                    board.add(this.addPiece(), row, col);
                }
            }
        }

        board.setOnMousePressed(event -> {
            for (Node node : board.getChildren()) {
                if (node instanceof Pane && !(node instanceof StackPane)) {
                    Pane tempPane = (Pane) node;
                    if (tempPane.getBoundsInParent().contains(event.getX(), event.getY())) { // mouse is in a pane
                        selectedPane = tempPane;
                        //tempPane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null))); if change of selected pane is needed
                        startCoordinates = new BoardCoordinate((int) Math.floor(event.getX()/100), whiteSideDown ? 8 - (int) Math.floor(event.getY()/100) : (int) Math.floor(event.getY()/100));
                    }
                }
            }
        });

        board.setOnMouseDragged(event -> {
            if (selectedPane == null) {
                return;
            }

            ImageView tempView = (ImageView) selectedPane.getChildren().get(0);

            double tempX = event.getX() - selectedPane.localToParent(0, 0).getX() - (tempView.getFitWidth() / 2);
            double tempY = event.getY() - selectedPane.localToParent(0, 0).getY() - (tempView.getFitHeight() / 2);
            tempView.setLayoutX(tempX);
            tempView.setLayoutY(tempY);
        });

        board.setOnMouseReleased(event -> {
            if (selectedPane == null) {
                return;
            }

            ImageView tempView = (ImageView) selectedPane.getChildren().get(0);

            if (event.getX() < 0 || event.getX() > board.getWidth() ||event.getY() < 0 || event.getY() > board.getHeight()) {
                tempView.setLayoutX(0);
                tempView.setLayoutY(0);
                return;
            }

            int tempX = (round((int) (event.getX()), 2) / 100);
            int tempY = (round((int) (event.getY()), 2) / 100);
            BoardCoordinate tempCoordinates = new BoardCoordinate(tempX, whiteSideDown ? 8 - (int) Math.floor(event.getY()/100) : tempY);
            GridPane.setRowIndex(selectedPane, tempY);
            GridPane.setColumnIndex(selectedPane, tempX);
            tempView.setLayoutX(0);
            tempView.setLayoutY(0);

            if (!Objects.equals(startCoordinates, tempCoordinates)) {
                //TODO add code here when a player moved a piece
                int moved;
                selected = false;
                //selectedPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null))); if color change of pane needed
                selectedPane = null;
            }

        });

        board.setOnMouseClicked(event -> {
            if (selectedPane == null) {
                return;
            }

            if (selected) {
                selected = false;
                ImageView tempView = (ImageView) selectedPane.getChildren().get(0);
                tempView.setLayoutX(round((int) event.getX(), 2));
                tempView.setLayoutY(round((int) event.getY(), 2));
                //TODO add code here when a player moved a piece
                int moved;
                selected = false;
                //selectedPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null))); if color change of pane needed
                selectedPane = null;
            }
        });
        stage.show();
    }

    private int round(int value, int position) {
        /**
         * Rounds value up to the nearest integer value based on position.
         * @param value: value of the integer to round.
         * @param position: position round up to. 1 = ones position, 2 = tens position, ...
         */

        int divider = (int) Math.pow(10, position);
        float tempValue = (float) value / divider;
        int tempChooser = value % divider;
        if (tempChooser > 85) { // value is bigger than 75 (e.g., 278 -> 300)
            return (int) (Math.ceil(tempValue) * divider);
        } else {
            return (int) (Math.floor(tempValue) * divider);
        }

    }

    private Pane addPiece() {
        Image image = new Image(getClass().getResourceAsStream("/images/pawn.png"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        Pane pane = new Pane();
        pane.prefWidthProperty().bind(imageView.fitWidthProperty());
        pane.prefHeightProperty().bind(imageView.fitHeightProperty());
        pane.getChildren().add(imageView);
        imageView.setLayoutX(0);
        imageView.setLayoutY(0);

        return pane;
    }
}
