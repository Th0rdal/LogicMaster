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

    // colors used for the board
    Color color1 = Color.SIENNA;
    Color color2 = Color.LIGHTGRAY;

    Pane selectedPane;  //represents the currently selected pane
    boolean selected = false; //true if a pane is currently selected
    BoardCoordinate startCoordinates;   //saves the start coordinations of the selected pane
    boolean whiteSideDown = true;   //TODO change. just a placeholder for board rotation. if true, white is starting on the bottom

    private Controller(Stage stage) {
        this.stage = stage;
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
        int size = 8;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Square square = new Square(row, col);
                this.board.add(square, row, col, 1, 1);
                Color color = (row + col) % 2 == 0 ? this.color1 : this.color2;
                square.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }

        Image image = new Image(getClass().getResourceAsStream("/images/pawn.png"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        Pane pane = new Pane();
        pane.getChildren().add(imageView);

        board.setOnMousePressed(event -> {
            for (Node node : board.getChildren()) {
                if (node instanceof Pane && !(node instanceof StackPane)) {
                    Pane tempPane = (Pane) node;
                    if (tempPane.getBoundsInParent().contains(event.getX(), event.getY())) { // mouse is in a pane
                        selectedPane = tempPane;
                        selected = true;
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

            tempView.setLayoutX(event.getX() - tempView.getFitWidth() / 2);
            tempView.setLayoutY(event.getY() - tempView.getFitHeight() / 2);
        });

        board.setOnMouseReleased(event -> {
            if (selectedPane == null) {
                return;
            }
            BoardCoordinate tempCoordinates = new BoardCoordinate((int) Math.floor(event.getX()/100), whiteSideDown ? 8 - (int) Math.floor(event.getY()/100) : (int) Math.floor(event.getY()/100));

            ImageView tempView = (ImageView) selectedPane.getChildren().get(0);
            tempView.setLayoutX(round((int) (tempView.getLayoutX() + tempView.getBoundsInParent().getWidth() / 2), 2));
            tempView.setLayoutY(round((int) (tempView.getLayoutY() + tempView.getBoundsInParent().getHeight() / 2), 2));

            if (!Objects.equals(startCoordinates, tempCoordinates)) {
                //TODO add code here when a player moved a piece
                int moved;
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
            }


        });

        board.add(pane, 0, 0);

        stage.show();
    }

    private int round(int value, int position) {
        /**
         * Rounds value up to the nearest integer value based on position.
         * @param value: value of the integer to round.
         * @param position: position round up to. 0 = ones position, 1 = tens position, ...
         */

        int divider = (int) Math.pow(10, position);
        int tempValue = value / divider;
        return Math.round(tempValue) * divider;

    }
}
