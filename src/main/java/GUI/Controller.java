package GUI;

import GUI.Player.Player;
import GUI.piece.*;
import GUI.utilities.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

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
    private Pane promotionPane;
    @FXML
    private GridPane moveHistoryGridPane;

    // colors used for the board
    Color color1 = Color.LIGHTGRAY;
    Color color2 = Color.SIENNA;
    private Color selectedMoveHistory = Color.LIGHTBLUE;
    private Color selectedColor = Color.web("#C6EDC3");
    private static Color selectedTextColor = Color.BLUE;
    private static Color defaultTextColor = Color.BLACK;

    private BlockingQueue<Move> moveQueue = null;
    ArrayList<Move> possibleMoveList;

    Pane selectedPane;  //represents the currently selected pane
    boolean selected = false; //true if a pane is currently selected
    BoardCoordinate startCoordinates;   //saves the start coordinations of the selected pane
    boolean whiteSideDown = true;   //TODO change. just a placeholder for board rotation. if true, white is starting on the bottom
    GameHandler gameHandler;
    private boolean inSnapshot = false;

    private Pane promotionSelectedPane = null;
    private Piece promotionPiece = null;
    private StackPane selectedHistoryTextPane = null;
    private Move move = null;

    private static final MouseEvent mouseClickedHistoryTextSelected = new MouseEvent(
                MouseEvent.MOUSE_CLICKED,
                0, 0,
                0, 0,
                MouseButton.PRIMARY,
                1, // click count
                true, true, true, true,
                true, true, true, true,
                true, true,
                null);

    //promotion event handling
    EventHandler<MouseEvent> onMouseMoveHandler = event -> {
        if (this.promotionSelectedPane != null) {
            if (this.promotionSelectedPane.getBoundsInParent().contains(event.getX(), event.getY())) {
                return;
            } else {
                this.promotionSelectedPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
            }
        }
        for (Node node : this.promotionPane.getChildren()) {
            if (node instanceof Pane) {
                Pane tempPane = (Pane) node;
                if (tempPane.getBoundsInParent().contains(event.getX(), event.getY())) {
                    promotionSelectedPane = tempPane;
                    promotionSelectedPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                }
            }
        }
    };

    // player movement event handling
    private final EventHandler<MouseEvent> playerOnMousePressed = event -> {
        for (Node node : visualBoard.getChildren()) {
            if (node instanceof Pane tempPane && !(node instanceof StackPane)) {
                if (tempPane.getBoundsInParent().contains(event.getX(), event.getY())) { // mouse is in a pane
                    tempPane.setBackground(new Background(new BackgroundFill(this.selectedColor, null, null)));
                    startCoordinates = new BoardCoordinate((int) Math.floor(event.getX() / 100) + 1, whiteSideDown ? 8 - (int) Math.floor(event.getY() / 100) : (int) Math.floor(event.getY() / 100));
                    if (this.gameHandler.isUsablePiece(startCoordinates)) {
                        selectedPane = tempPane;
                    } else if (this.selectedPane == null) {
                        startCoordinates = null;
                    }
                    break;
                }
            }
        }
        if (startCoordinates != null) {
            possibleMoveList = this.gameHandler.getPossibleMovesForCoordinates(startCoordinates);
            if (possibleMoveList == null || possibleMoveList.isEmpty()) {
                return;
            }
            visualBoard.getChildren().removeIf(node -> node instanceof Circle);
            for (Move move : possibleMoveList) {
                Circle tempCircle = new Circle();
                visualBoard.add(tempCircle, move.getNewPosition().getXLocation()-1, whiteSideDown ? 8 - move.getNewPosition().getYLocation() : move.getNewPosition().getYLocation());
                GridPane.setHalignment(tempCircle, HPos.CENTER);
                GridPane.setValignment(tempCircle, VPos.CENTER);
            }
        }
    };

    private final EventHandler<MouseEvent> playerOnMouseDragged = event -> {
            if (selectedPane == null) {
                return;
            }

            selectedPane.toFront();

            ImageView tempView = (ImageView) selectedPane.getChildren().get(0);

            double tempX = event.getX() - selectedPane.localToParent(0, 0).getX() - (tempView.getFitWidth() / 2);
            double tempY = event.getY() - selectedPane.localToParent(0, 0).getY() - (tempView.getFitHeight() / 2);
            tempView.setLayoutX(tempX);
            tempView.setLayoutY(tempY);
    };
    private final EventHandler<MouseEvent> playerOnMouseReleased = event -> {
        if (selectedPane != null) {
            this.checkMovePossible((int) event.getX(), (int) event.getY());
        }
    };

    //promotion event handler
    private final EventHandler<MouseEvent> onMouseClickHandler = event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
            // chosen
            switch (this.promotionSelectedPane.getId()) {
                case "KNIGHT" -> this.move.setPromotionID(PIECE_ID.KNIGHT);
                case "QUEEN" -> this.move.setPromotionID(PIECE_ID.QUEEN);
                case "BISHOP" -> this.move.setPromotionID(PIECE_ID.BISHOP);
                case "ROOK" -> this.move.setPromotionID(PIECE_ID.ROOK);
                default -> throw new RuntimeException("Promoting piece into something that is not allowed");
            };
            this.promotionPane.setOnMouseMoved(null);
            this.promotionPane.setOnMouseClicked(null);
            this.promotionPane.setVisible(false);
        } else if (event.getButton() == MouseButton.SECONDARY) { //TODO find out purpose
            this.promotionSelectedPane.setVisible(false);
        }
    };

    public Controller(Stage stage) {
        this.stage = stage;
        this.gameHandler = new GameHandler();
        this.gameHandler.setWhitePlayer(new Player(false, "algorithms/algorithm.exe", "IAN"));
        this.gameHandler.setBlackPlayer(new Player(false, "algorithms/algorithm.exe", "BOT"));
        this.gameHandler.setController(this);
        new Thread(() -> {
            this.gameHandler.gameLoop();
        }).start();
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
        //TODO add gameHandler clearBoard function
        //this.gameHandler.clearBoard();
    }

    /**
     * Clears everything and starts a new board
     */
    public void startNewBoard() {
        this.clearBoard();
        //TODO add gameHandler loadStartPosition function
        //this.gameHandler.loadStartPosition();
        this.loadBoard();
    }

    public void loadPromotionPane(boolean isWhite) {
        //399 because of weird calc with border.
        ImageView view = new ImageView(ImageLoader.getImage(PIECE_ID.ROOK, isWhite));
        Pane pane = new Pane(view);
        view.setFitWidth(399);
        view.setFitHeight(399);
        this.promotionPane.getChildren().add(pane);
        pane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        pane.setId("ROOK");

        view = new ImageView(ImageLoader.getImage(PIECE_ID.BISHOP, isWhite));
        pane = new Pane(view);
        view.setFitWidth(399);
        view.setFitHeight(399);
        pane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        this.promotionPane.getChildren().add(pane);
        pane.setLayoutX(400);
        pane.setLayoutY(0);
        pane.setId("BISHOP");


        view = new ImageView(ImageLoader.getImage(PIECE_ID.KNIGHT, isWhite));
        pane = new Pane(view);
        view.setFitWidth(399);
        view.setFitHeight(399);
        pane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        this.promotionPane.getChildren().add(pane);
        pane.setLayoutX(400);
        pane.setLayoutY(400);
        pane.setId("KNIGHT");


        view = new ImageView(ImageLoader.getImage(PIECE_ID.QUEEN, isWhite));
        pane = new Pane(view);
        view.setFitWidth(399);
        view.setFitHeight(399);
        pane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        this.promotionPane.getChildren().add(pane);
        pane.setLayoutX(0);
        pane.setLayoutY(400);
        pane.setId("QUEEN");
    }

    /**
     * creates the board visually and adds all the pieces based on the board variable
     * adds event listeners for piece movement
     */
    public void loadBoard() {
        /*
         * This function loads the board with squares and gives each square a chess board color
         */

        this.clockOpponentLabel.setFont(Font.font("Arial", 22));
        clockOpponentLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        this.clockPlayerLabel.setFont(Font.font("Arial", 22));
        clockPlayerLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

        // adding squares to board
        int size = 8;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Square square = new Square(row+1, col+1);
                this.visualBoard.add(square, row, col, 1, 1);
                Color color = (row + col) % 2 == 0 ? this.color1 : this.color2;
                square.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }

        loadPieces();

        this.gameHandler.loadClocks(this.clockOpponentLabel, this.clockPlayerLabel);

        promotionPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        promotionPane.setVisible(false);

        // TODO fix bug that starting with 3rd row, the rows are too small
        this.moveHistoryGridPane.setHgap(45);
        this.moveHistoryGridPane.setVgap(8);
        ColumnConstraints column1 = new ColumnConstraints(45);
        ColumnConstraints column2 = new ColumnConstraints(45);
        column1.setMaxWidth(45);
        column2.setMaxWidth(45);
        RowConstraints row1 = new RowConstraints(50);
        RowConstraints row2 = new RowConstraints(50);
        row1.setMinHeight(50);
        row2.setMinHeight(50);
        row1.setMaxHeight(50);
        row2.setMaxHeight(50);
        this.moveHistoryGridPane.getColumnConstraints().addAll(column1, column2);
        this.moveHistoryGridPane.getRowConstraints().addAll(row1, row2);

        stage.show();
    }

    public void loadPieces() {
        this.visualBoard.getChildren().removeIf(node -> node instanceof Pane && !(node instanceof StackPane));
        for (Piece piece : this.gameHandler.getPieces()) {
            visualBoard.add(piece.getPieceImage(), piece.getLocationX()-1, whiteSideDown ? 8 - piece.getLocationY() : piece.getLocationY());
        }
    }

    public void loadPieces(int moveNumber) {
        this.visualBoard.getChildren().removeIf(node -> node instanceof Pane && !(node instanceof StackPane));
        for (Piece piece : this.gameHandler.getSnapshot(moveNumber).getPieces()) {
            visualBoard.add(piece.getPieceImage(), piece.getLocationX()-1, whiteSideDown ? 8 - piece.getLocationY() : piece.getLocationY());
        }
    }

    public void setPlayerEventHandling(BlockingQueue<Move> moveQueue) {
        visualBoard.addEventFilter(MouseEvent.MOUSE_PRESSED, this.playerOnMousePressed);
        visualBoard.addEventFilter(MouseEvent.MOUSE_DRAGGED, this.playerOnMouseDragged);
        visualBoard.addEventFilter(MouseEvent.MOUSE_RELEASED, this.playerOnMouseReleased);
        this.moveQueue = moveQueue;
    }

    void resetPlayerEventHandling() {
        visualBoard.removeEventFilter(MouseEvent.MOUSE_PRESSED, playerOnMousePressed);
        visualBoard.removeEventFilter(MouseEvent.MOUSE_DRAGGED, playerOnMouseDragged);
        visualBoard.removeEventFilter(MouseEvent.MOUSE_RELEASED, playerOnMouseReleased);
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

        BoardCoordinate tempCoordinates = new BoardCoordinate(tempX+1, whiteSideDown ? 8 - (int) (double) (y / 100) : tempY);

        if (startCoordinates.equals(tempCoordinates)) {
            tempView.setLayoutX(0);
            tempView.setLayoutY(0);
            return;
        }

        for (Move move : this.possibleMoveList) {
            if (move.getNewPosition().equals(tempCoordinates)) {
                this.move = move;
            }
        }
        if (this.move != null) {
            this.resetSelected();

            if (move.getSpecialMove() == SPECIAL_MOVE.PROMOTION) { // promotion logic
                //this.promotionPiece = this.gameHandler.getPieceAtCoordinates(startCoordinates);
                loadPromotionPane(this.promotionPiece.isWhite());
                this.promotionPane.setVisible(true);
                this.promotionPane.setOnMouseMoved(this.onMouseMoveHandler);
                this.promotionPane.setOnMouseClicked(this.onMouseClickHandler);
            }

            try {
                this.moveQueue.put(move);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.resetSelected();
        }
    }

    private void resetSelected() {
        ImageView tempView = (ImageView) selectedPane.getChildren().get(0);
        tempView.setLayoutX(0);
        tempView.setLayoutY(0);
        selectedPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        selectedPane = null;
        visualBoard.getChildren().removeIf(node -> node instanceof Circle);
    }

    public void addMoveToMovelist(Move move) {
        Platform.runLater(() -> {
            int moveCounter = this.gameHandler.getFullmoveClock();
            Text temp = new Text(moveCounter + ": " + move.toString());
            StackPane tempPane = new StackPane();
            tempPane.getChildren().add(temp);

            tempPane.setOnMouseClicked(event -> {
                if (this.selectedHistoryTextPane != null) {
                    ((Text)this.selectedHistoryTextPane.getChildren().get(0)).setFill(Controller.defaultTextColor);
                    this.selectedHistoryTextPane.getChildren().get(0).setStyle("");
                    this.selectedHistoryTextPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                    int currentFieldNumber = Integer.parseInt((
                            (Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().substring(0,
                            ((Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().indexOf(':')));
                    int moveNumber = Integer.parseInt(temp.getText().substring(0, temp.getText().indexOf(':')));
                    if (moveNumber !=  this.gameHandler.getFullmoveClock() || currentFieldNumber != this.gameHandler.getFullmoveClock()) {
                        this.loadPieces(moveNumber);
                    }
                }

                tempPane.setBackground(new Background(new BackgroundFill(selectedMoveHistory, CornerRadii.EMPTY, Insets.EMPTY)));

                ((Text) tempPane.getChildren().get(0)).setFill(Controller.selectedTextColor);
                ((Text)tempPane.getChildren().get(0)).setStyle("-fx-font-weight: bold;");
                this.selectedHistoryTextPane = tempPane;
            });

            // if selectedHistoryText is the last move taken or is empty, fire event
            if (this.selectedHistoryTextPane != null &&
                    Integer.parseInt(((Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().substring(0, ((Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().indexOf(':'))) + 1 == moveCounter) {
                temp.fireEvent(Controller.mouseClickedHistoryTextSelected);
            } else if (this.selectedHistoryTextPane == null) {
                temp.fireEvent(Controller.mouseClickedHistoryTextSelected);
            }

            this.moveHistoryGridPane.add(tempPane, (moveCounter-1)%2, (moveCounter-1)/2);
        });
    }
}
