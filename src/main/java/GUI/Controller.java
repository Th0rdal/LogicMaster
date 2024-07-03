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
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
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
    @FXML
    private Pane promotionPane;

    // colors used for the board
    Color color1 = Color.LIGHTGRAY;
    Color color2 = Color.SIENNA;
    private Color selectedColor = Color.web("#C6EDC3");

    ArrayList<Move> possibleMoveList;

    Pane selectedPane;  //represents the currently selected pane
    boolean selected = false; //true if a pane is currently selected
    BoardCoordinate startCoordinates;   //saves the start coordinations of the selected pane
    boolean whiteSideDown = true;   //TODO change. just a placeholder for board rotation. if true, white is starting on the bottom
    Gamestate gamestate;

    private Pane promotionSelectedPane = null;
    private Piece promotionPiece = null;

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
                    if (this.gamestate.isUsablePiece(startCoordinates)) {
                        selectedPane = tempPane;
                    } else {
                        startCoordinates = null;
                    }
                    break;
                }
            }
        }
        if (startCoordinates != null) {
            possibleMoveList = this.gamestate.getPossibleMovesForCoordinates(startCoordinates);
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
            Piece piece = switch (this.promotionSelectedPane.getId()) {
                case "KNIGHT" -> this.gamestate.promotePawn(this.promotionPiece, PIECE_ID.KNIGHT);
                case "QUEEN" -> this.gamestate.promotePawn(this.promotionPiece, PIECE_ID.QUEEN);
                case "BISHOP" -> this.gamestate.promotePawn(this.promotionPiece, PIECE_ID.BISHOP);
                case "ROOK" -> this.gamestate.promotePawn(this.promotionPiece, PIECE_ID.ROOK);
                default -> throw new RuntimeException("Promoting piece into something that is not allowed");
            };
            this.visualBoard.getChildren().remove(this.promotionPiece.getPieceImage());
            this.visualBoard.add(piece.getPieceImage(), piece.getLocationX()-1, whiteSideDown ? 8 - piece.getLocationY() : piece.getLocationY());
            this.gamestate.getSemaphore().release();
            this.promotionPane.setOnMouseMoved(null);
            this.promotionPane.setOnMouseClicked(null);
            this.promotionPane.setVisible(false);
        } else if (event.getButton() == MouseButton.SECONDARY) {
            this.promotionSelectedPane.setVisible(false);
        }
    };

    private Controller(Stage stage) {
        this.stage = stage;
        this.gamestate = new Gamestate();
        this.gamestate.setWhitePlayer(new Player(false, "algorithms/algorithm.exe", "IAN"));
        this.gamestate.setBlackPlayer(new Player(false, "algorithms/algorithm.exe", "BOT"));
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
        this.gamestate.clearBoard();
    }

    /**
     * Clears everything and starts a new board
     */
    public void startNewBoard() {
        this.clearBoard();
        this.gamestate.loadStartPosition();
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

        for (Piece piece : this.gamestate.getPieces()) {
            visualBoard.add(piece.getPieceImage(), piece.getLocationX()-1, whiteSideDown ? 8 - piece.getLocationY() : piece.getLocationY());
        }

        this.gamestate.loadClocks(this.clockOpponentLabel, this.clockPlayerLabel);

        promotionPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        promotionPane.setVisible(false);

        if (this.gamestate.currentPlayerHuman()) {
            this.setPlayerEventHandling();
        } else {
            new Thread(this::aiMove).start();
        }

        stage.show();
    }

    void setPlayerEventHandling() {
        visualBoard.addEventFilter(MouseEvent.MOUSE_PRESSED, this.playerOnMousePressed);
        visualBoard.addEventFilter(MouseEvent.MOUSE_DRAGGED, this.playerOnMouseDragged);
        visualBoard.addEventFilter(MouseEvent.MOUSE_RELEASED, this.playerOnMouseReleased);
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

        Move move = this.gamestate.getMoveFromPossibleMoves(this.possibleMoveList, tempCoordinates);
        Piece piece = this.gamestate.getPieceAtCoordinates(tempCoordinates);
        if (move != null) {
            if (move.isCapture()) {
                this.visualBoard.getChildren().remove(piece.getPieceImage());
            }
            if (move.getSpecialMove() == SPECIAL_MOVE.KING_CASTLE) {
                Pane king = this.gamestate.getPieceAtCoordinates(move.getOldPosition()).getPieceImage();
                Pane rook;
                if (this.gamestate.isWhiteTurn()) {
                    rook = this.gamestate.getPieceAtCoordinates(new BoardCoordinate("H1")).getPieceImage();
                    GridPane.setRowIndex(king, this.whiteSideDown ? 8 : 0);
                    GridPane.setColumnIndex(king, 6);
                    GridPane.setRowIndex(rook, this.whiteSideDown ? 8 : 0);
                    GridPane.setColumnIndex(rook, 5);
                } else {
                    rook = this.gamestate.getPieceAtCoordinates(new BoardCoordinate("H8")).getPieceImage();
                    GridPane.setRowIndex(king, this.whiteSideDown ? 0 : 8);
                    GridPane.setColumnIndex(king, 6);
                    GridPane.setRowIndex(rook, this.whiteSideDown ? 0 : 8);
                    GridPane.setColumnIndex(rook, 5);
                }
            } else if (move.getSpecialMove() == SPECIAL_MOVE.QUEEN_CASTLE) {
                Pane king = this.gamestate.getPieceAtCoordinates(move.getOldPosition()).getPieceImage();
                Pane rook;
                if (this.gamestate.isWhiteTurn()) {
                    rook = this.gamestate.getPieceAtCoordinates(new BoardCoordinate("A1")).getPieceImage();
                    GridPane.setRowIndex(rook, this.whiteSideDown ? 8 : 0);
                    GridPane.setColumnIndex(rook, 3);
                } else {
                    rook = this.gamestate.getPieceAtCoordinates(new BoardCoordinate("A8")).getPieceImage();
                    GridPane.setRowIndex(rook, this.whiteSideDown ? 0 : 8);
                    GridPane.setColumnIndex(rook, 3);
                }
            } else if (move.getSpecialMove() == SPECIAL_MOVE.EN_PASSANT) {
                int adding = 0;
                if ((this.gamestate.isWhiteTurn() && this.whiteSideDown) || (!this.gamestate.isWhiteTurn() && !this.whiteSideDown)) {
                    adding = 1;
                } else if ((this.gamestate.isWhiteTurn() && !this.whiteSideDown) ||(!this.gamestate.isWhiteTurn() && this.whiteSideDown)) {
                    adding = -1;
                }
                Piece pawn = this.gamestate.getPieceAtCoordinates(new BoardCoordinate(move.getNewPosition().getXLocation(),
                        this.whiteSideDown ? 8 - move.getNewPosition().getYLocation() : move.getNewPosition().getYLocation() + adding));
                this.visualBoard.getChildren().remove(pawn.getPieceImage());
            }

            GridPane.setRowIndex(selectedPane, tempY);
            GridPane.setColumnIndex(selectedPane, tempX);
            this.resetSelected();

            if (move.getSpecialMove() == SPECIAL_MOVE.PROMOTION) { // promotion logic
                this.promotionPiece = this.gamestate.getPieceAtCoordinates(startCoordinates);
                loadPromotionPane(this.promotionPiece.isWhite());
                this.promotionPane.setVisible(true);
                this.promotionPane.setOnMouseMoved(this.onMouseMoveHandler);
                this.promotionPane.setOnMouseClicked(this.onMouseClickHandler);
            }
            new Thread(() -> {
                this.makeMove(move);
            }).start();
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

    private void makeMove(Move move) {
        selectedPane = null;
        //selectedPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null))); if color change of pane needed
        if (this.gamestate.currentPlayerHuman()) {
            this.resetPlayerEventHandling();
        }
        gamestate.makeMove(move);
        if (!this.gamestate.currentPlayerHuman()) {
            this.aiMove();
        }
        this.setPlayerEventHandling();
    }

    private void aiMove() {
        while (!this.gamestate.currentPlayerHuman()) {
            Move aiMove = this.gamestate.executeAlgorithm();

            Piece piece = this.gamestate.getPieceAtCoordinates(aiMove.getNewPosition());
            if (aiMove.isCapture()) {
                Platform.runLater(() -> {
                    this.visualBoard.getChildren().remove(piece.getPieceImage());
                });
            }

            this.gamestate.makeMove(aiMove);

            // piece movement
            Pane piecePane = null;
            for (Node node : this.visualBoard.getChildren()) {
                if (node instanceof Pane && !(node instanceof StackPane)) {
                    int tempY = this.whiteSideDown ? 8 - aiMove.getOldPosition().getYLocation() : aiMove.getOldPosition().getYLocation() - 1;
                    if (GridPane.getRowIndex(node) == tempY &&
                        GridPane.getColumnIndex(node) == aiMove.getOldPosition().getXLocation() - 1) {
                        piecePane = (Pane) node;
                        break;
                    }
                }
            }
            if (piecePane == null) {
                throw new RuntimeException();
            }
            ImageView tempView = (ImageView) piecePane.getChildren().get(0);
            tempView.setLayoutX(0);
            tempView.setLayoutY(0);
            GridPane.setRowIndex(piecePane, this.whiteSideDown ? 8 - aiMove.getNewPosition().getYLocation() : aiMove.getNewPosition().getYLocation() - 1);
            GridPane.setColumnIndex(piecePane, aiMove.getNewPosition().getXLocation() - 1);
        }
    }
}
