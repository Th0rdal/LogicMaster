package GUI.controller;

import GUI.Config;
import GUI.exceptions.ObjectInterruptedException;
import GUI.game.*;
import GUI.game.gamestate.CHECKMATE_TYPE;
import GUI.game.gamestate.GamestateSnapshot;
import GUI.game.move.Move;
import GUI.game.move.SPECIAL_MOVE;
import GUI.handler.GameHandler;
import GUI.handler.SceneHandler;
import GUI.piece.*;
import GUI.UIElements.Circle;
import GUI.utilities.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * The controller used by the board.fxml page
 */
public class BoardController {
    private static final int moveHistoryGridPaneHeight = 50;
    @FXML
    private GridPane visualBoard;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label clockWhiteLabel;
    @FXML
    private Label clockBlackLabel;
    @FXML
    private Pane promotionPane;
    @FXML
    private GridPane moveHistoryGridPane;
    @FXML
    private Button drawButton;
    @FXML
    private Button backButton;
    @FXML
    private Button saveButton;
    @FXML
    private ScrollPane moveHistoryScrollPane;
    @FXML
    private GridPane boardRowHeaders;
    @FXML
    private GridPane boardColumnHeaders;
    @FXML
    private CheckBox turnBoardAfterMoveCheckBox;
    @FXML
    private Button turnBoardButton;
    @FXML
    private VBox leftSideBar;

    private BlockingQueue<Move> moveQueue = null;
    private ArrayList<Move> possibleMoveList;

    private Pane selectedPane;  //represents the currently selected pane
    private BoardCoordinate startCoordinates;   //saves the start coordinations of the selected pane
    private boolean whiteSideDown = true; // true if the starting position of the white side is down
    private GameHandler gameHandler;

    private Pane promotionSelectedPane = null;
    private StackPane selectedHistoryTextPane = null;
    private Move move = null;

    /**
     * mouse event being fired if a new move is made and the move list should set the new move as selected
     */
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

    /** handles what happens when the mouse is pressed. Selects the current field for highlighting */
    private final EventHandler<MouseEvent> playerOnMousePressed = event -> {
        for (Node node : visualBoard.getChildren()) {
            if (node instanceof Pane tempPane && !(node instanceof StackPane)) {
                if (tempPane.getBoundsInParent().contains(event.getX(), event.getY())) { // mouse is in a pane
                    int tempX = (int) Math.floor(event.getX() / 100);
                    int tempY = (int) Math.floor(event.getY() / 100);
                    startCoordinates = new BoardCoordinate(
                            whiteSideDown ? tempX + 1 : 8 - tempX,
                            whiteSideDown ? 8 - tempY : tempY+1);

                    if (this.gameHandler.isInSnapshot()) { // snapshot
                        int currentFieldNumber = Integer.parseInt((
                            (Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().substring(0,
                            ((Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().indexOf(':')));

                        // currentFieldNumber % 2 != 1, because you need to check for the enemy. If it is snapshot turn 2,
                        // then it is whites turn
                        if (!this.gameHandler.isUsablePieceSnapshot(startCoordinates, currentFieldNumber)) {
                            startCoordinates = null;
                            break;
                        }
                        if (AlertHandler.showConfirmationAlertAndWait("New game?",
                                "You are currently in a snapshot of a previous move. Do you wish to start a new game from this position?")) {
                            if (AlertHandler.showConfirmationAlertAndWait("save old game?", "Do you wish to save the game in the database?")) {
                                this.gameHandler.saveCurrentInDatabase();
                            }
                            new Thread(() -> { // new thread so javafx thread never has to wait for anything
                                this.gameHandler.setContinueFromSnapshotFlag(Integer.parseInt((
                                    (Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().substring(0,
                                    ((Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().indexOf(':'))));
                            }).start();
                        }
                        startCoordinates = null;
                        break;
                    } else { // not snapshot
                        if (this.gameHandler.isUsablePiece(startCoordinates)) {
                            if (selectedPane != null) {
                                selectedPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
                            }
                            selectedPane = tempPane;
                            selectedPane.setBackground(new Background(new BackgroundFill(Config.selectedColor, null, null)));
                        } else if (this.selectedPane == null) {
                            startCoordinates = null;
                        }
                        break;
                    }
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
                if (move.getSpecialMove() == SPECIAL_MOVE.KING_CASTLE) {
                    if (this.gameHandler.isTurnWhite()) {
                        visualBoard.add(tempCircle,
                                whiteSideDown ? 6 : 5,
                                whiteSideDown ? 7 : 0);
                    } else {
                        visualBoard.add(tempCircle,
                            whiteSideDown ? 6 : 5,
                            whiteSideDown ? 0 : 7);
                    }
                } else if (move.getSpecialMove() == SPECIAL_MOVE.QUEEN_CASTLE) {
                    if (this.gameHandler.isTurnWhite()) {
                        visualBoard.add(tempCircle,
                                whiteSideDown ? 2 : 3,
                                whiteSideDown ? 7 : 0);
                    } else {
                        visualBoard.add(tempCircle,
                            whiteSideDown ? 2 : 3,
                            whiteSideDown ? 0 : 7);
                    }
                } else {
                    visualBoard.add(tempCircle,
                            whiteSideDown ? move.getNewPosition().getXLocation()-1 : 8 - move.getNewPosition().getXLocation(),
                            whiteSideDown ? 8 - move.getNewPosition().getYLocation() : move.getNewPosition().getYLocation()-1);
                }
                GridPane.setHalignment(tempCircle, HPos.CENTER);
                GridPane.setValignment(tempCircle, VPos.CENTER);
            }
        }
    };

    /** handles mouse dragging when a piece is selected */
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

    /** handles when the mouse is released. If a pane is selected, it checks if a move is possible */
    private final EventHandler<MouseEvent> playerOnMouseReleased = event -> {
        if (selectedPane != null) {
            this.checkMovePossible((int) event.getX(), (int) event.getY());
        }
    };

    /** handles mouse clicking while in the promotion pane */
    private final EventHandler<MouseEvent> onMouseClickHandler = event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
            // chosen
            PIECE_ID id = switch (this.promotionSelectedPane.getId()) {
                case "KNIGHT" -> PIECE_ID.KNIGHT;
                case "QUEEN" -> PIECE_ID.QUEEN;
                case "BISHOP" -> PIECE_ID.BISHOP;
                case "ROOK" -> PIECE_ID.ROOK;
                default -> {
                    String message = MessageFormat.format("Promoting piece into something that is not allowed ({0})", this.promotionSelectedPane.getId());
                    AlertHandler.throwError();
                    throw new IllegalArgumentException(message);
                }
            };

            for (Move move : this.possibleMoveList) {
                if (move.getSpecialMove() == SPECIAL_MOVE.PROMOTION && move.getPromotion_ID() == id) {
                    this.move = move;
                }
            }
            this.promotionPane.setOnMouseMoved(null);
            this.promotionPane.setOnMouseClicked(null);
            this.promotionPane.setVisible(false);
            this.putMove();
        } else if (event.getButton() == MouseButton.SECONDARY) {
            this.promotionPane.setVisible(!this.promotionPane.isVisible());
        }
    };

    /** handles the highlighting in the promotion pane */
    private final EventHandler<MouseEvent> onMouseMoveHandler = event -> {
        if (this.promotionSelectedPane != null) {
            if (this.promotionSelectedPane.getBoundsInParent().contains(event.getX(), event.getY())) {
                return;
            } else {
                this.promotionSelectedPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
            }
        }
        for (Node node : this.promotionPane.getChildren()) {
            if (node instanceof Pane tempPane) {
                if (tempPane.getBoundsInParent().contains(event.getX(), event.getY())) {
                    promotionSelectedPane = tempPane;
                    promotionSelectedPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                }
            }
        }
    };


    /**
     * loads the promotion pane with the given pormotion possibilities
     * @param isWhite: true if white promotes, else false
     */
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
    public void loadElements() {
        /*
         * This function loads the board with squares and gives each square a chess board color
         */

        // config buttons
        this.drawButton.setOnAction(event -> {
            if (!this.gameHandler.getPlayer(this.gameHandler.isTurnWhite()).isHuman()) {
                return;
            }

            if (this.gameHandler.canDrawFiftyMoves()) {
                this.setCheckmateAlert(CHECKMATE_TYPE.FIFTY_MOVE_RULE, this.gameHandler.isTurnWhite());
                Move move = new Move(true);
                try {
                    this.moveQueue.put(move);
                } catch (InterruptedException e) {
                    AlertHandler.throwError();
                    throw new ObjectInterruptedException("Move queue interrupted unexpectedly", e);
                }
            } else {
                if (this.gameHandler.getPlayer(!this.gameHandler.isTurnWhite()).isHuman()) {
                    String player = this.gameHandler.getPlayer(this.gameHandler.isTurnWhite()).getName();
                    boolean result = AlertHandler.showConfirmationAlertAndWait("Draw offer", player + " offered a draw. Do you want to accept?");
                    if (result) {
                        Move move = new Move(true);
                        try {
                            this.moveQueue.put(move);
                        } catch (InterruptedException e) {
                            AlertHandler.throwError();
                            throw new ObjectInterruptedException("Move queue interrupted unexpectedly", e);
                        }
                    }
                } else {
                    AlertHandler.showAlert(AlertType.INFORMATION, "draw declined", "The bot declined the draw.");
                }
            }
        });

        this.backButton.setOnAction(event -> {
            this.gameHandler.setInterruptFlag();
            SceneHandler.getInstance().activate("index");
        });

        this.saveButton.setOnAction(event -> {
            AlertHandler.showAlert(AlertType.INFORMATION, "saved game", "The game was saved in the database");
            this.gameHandler.saveCurrentInDatabase();
        });

        this.turnBoardButton.setOnAction(event -> {
            this.whiteSideDown = !this.whiteSideDown;
            this.loadBoard();
            int index1 = this.leftSideBar.getChildren().indexOf(this.clockWhiteLabel);
            int index2 = this.leftSideBar.getChildren().indexOf(this.clockBlackLabel);

            this.leftSideBar.getChildren().remove(this.clockWhiteLabel);
            this.leftSideBar.getChildren().remove(this.clockBlackLabel);

            if (index1 < index2) {
                this.leftSideBar.getChildren().add(index1, this.clockBlackLabel);
                this.leftSideBar.getChildren().add(index2, this.clockWhiteLabel);
            } else {
                this.leftSideBar.getChildren().add(index2, this.clockWhiteLabel);
                this.leftSideBar.getChildren().add(index1, this.clockBlackLabel);
            }
        });

        this.clockWhiteLabel.setFont(Font.font("Arial", 22));
        clockWhiteLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        this.clockBlackLabel.setFont(Font.font("Arial", 22));
        clockBlackLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

        // adding squares to board
        int size = 8;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane square = new StackPane();
                this.visualBoard.add(square, row, col, 1, 1);
                Color color = (row + col) % 2 == 0 ? Config.squareColorWhite : Config.squareColorBlack;
                square.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }

        promotionPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        promotionPane.setVisible(false);

        this.moveHistoryGridPane.setHgap(12);
        this.moveHistoryScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    }

    /**
     * loads the board. That consists in loading the board row headers, board column headers, the clocks and the pieces.
     */
    public void loadBoard() {
        for (Node node : this.boardRowHeaders.getChildren()) {
            String text = this.whiteSideDown ? String.valueOf(8 - GridPane.getRowIndex(node)) : String.valueOf(GridPane.getRowIndex(node)+1);
            ((Label)node).setText(text);
        }

        for (Node node : this.boardColumnHeaders.getChildren()) {
            String text = this.whiteSideDown ? Character.toString((char) 65 + GridPane.getColumnIndex(node)) : Character.toString((char) 65 + 7 - GridPane.getColumnIndex(node));
            ((Label)node).setText(text);
        }

        boolean clockUsed;
        if (this.whiteSideDown) {
            clockUsed = this.gameHandler.loadClocks(this.clockBlackLabel, this.clockWhiteLabel);
        } else {
            clockUsed = this.gameHandler.loadClocks(this.clockWhiteLabel, this.clockBlackLabel);
        }
        if (!clockUsed) {
            this.clockWhiteLabel.setVisible(false);
            this.clockBlackLabel.setVisible(false);
        }

        loadPieces();
    }

    /**
     * loads the pieces into the board. It uses the getPieces method from gameHandler to get the piece positions.
     */
    public void loadPieces() {
        this.visualBoard.getChildren().removeIf(node -> node instanceof Pane && !(node instanceof StackPane));
        for (Piece piece : this.gameHandler.getPieces()) {
            visualBoard.add(piece.getPieceImage(100),
                    whiteSideDown ? piece.getLocationX()-1 : 8 - piece.getLocationX(),
                    whiteSideDown ? 8 - piece.getLocationY() : piece.getLocationY()-1);
        }
    }

    /**
     * loads the pieces into the board. It gets the piece positions by using a snapshot from gameHandler.
     * @param moveNumber: the move number of the snapshot to load
     */
    public void loadPieces(int moveNumber) {
        this.visualBoard.getChildren().removeIf(node -> node instanceof Pane && !(node instanceof StackPane));
        for (Piece piece : this.gameHandler.getSnapshot(moveNumber).getPieces()) {
            visualBoard.add(piece.getPieceImage(100),
                    whiteSideDown ? piece.getLocationX()-1 : 8 - piece.getLocationX(),
                    whiteSideDown ? 8 - piece.getLocationY() : piece.getLocationY()-1);
        }
    }

    /**
     * sets the event filter for the player to move pieces
     * @param moveQueue: the queue to put the move in after it is done
     */
    public void setPlayerEventHandling(BlockingQueue<Move> moveQueue) {
        visualBoard.addEventFilter(MouseEvent.MOUSE_PRESSED, this.playerOnMousePressed);
        visualBoard.addEventFilter(MouseEvent.MOUSE_DRAGGED, this.playerOnMouseDragged);
        visualBoard.addEventFilter(MouseEvent.MOUSE_RELEASED, this.playerOnMouseReleased);
        this.moveQueue = moveQueue;
    }

    /**
     * removes the event filter (players can no longer move pieces)
     */
    public void resetPlayerEventHandling() {
        visualBoard.removeEventFilter(MouseEvent.MOUSE_PRESSED, playerOnMousePressed);
        visualBoard.removeEventFilter(MouseEvent.MOUSE_DRAGGED, playerOnMouseDragged);
        visualBoard.removeEventFilter(MouseEvent.MOUSE_RELEASED, playerOnMouseReleased);
    }

    /**
     * checks if a move is possible based on the position of the mouse
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

        BoardCoordinate tempCoordinates = new BoardCoordinate(
                whiteSideDown ? tempX+1 : 8 - tempX,
                whiteSideDown ? 8 - tempY : tempY+1);
        System.out.println(tempCoordinates);

        if (startCoordinates.equals(tempCoordinates)) {
            tempView.setLayoutX(0);
            tempView.setLayoutY(0);
            return;
        }

        this.move = null;
        for (Move move : this.possibleMoveList) {
            if (move.getNewPosition().equals(tempCoordinates) || move.isCastlingMove(tempCoordinates)) {
                this.move = move;
            }
        }

        if (this.move != null) {

            this.resetSelected();

            if (move.getSpecialMove() == SPECIAL_MOVE.PROMOTION) { // promotion logic
                loadPromotionPane(this.gameHandler.isTurnWhite());
                this.promotionPane.setVisible(true);
                this.promotionPane.setOnMouseMoved(this.onMouseMoveHandler);
                this.promotionPane.setOnMouseClicked(this.onMouseClickHandler);
            } else {
                this.putMove();
            }
        } else {
            this.resetSelected();
        }
    }

    /**
     * add a move to the move queue
     */
    private void putMove() {
        try {
            this.moveQueue.put(move);
        } catch (InterruptedException e) {
            AlertHandler.throwError();
            throw new ObjectInterruptedException("Move queue interrupted unexpectedly", e);
        }
        this.afterMove();
    }

    /**
     * executes functions to be executed after a move was made.
     * This is currently only turning the board if the corresponding check box is set
     */
    public void afterMove() {
        if (this.turnBoardAfterMoveCheckBox.isSelected()) {
            this.turnBoardButton.fire();
        }
    }

    /**
     * resets the currently selected pane
     */
    private void resetSelected() {
        ImageView tempView = (ImageView) selectedPane.getChildren().get(0);
        tempView.setLayoutX(0);
        tempView.setLayoutY(0);
        selectedPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        selectedPane = null;
        visualBoard.getChildren().removeIf(node -> node instanceof Circle);
    }

    /**
     * adds a move to the move list on the right side of the board.
     * @param move: the move to add
     * @param numberMove: the move number (used to calculate the position in the list)
     */
    public void addMoveToMovelist(Move move, int numberMove) {
        Platform.runLater(() -> {
            int moveCounter = numberMove - 1;
            Text temp = new Text(numberMove + ": " + move.toString()); //-1 because gamestate is already incremented
            StackPane tempPane = new StackPane();
            tempPane.getChildren().add(temp);

            tempPane.setOnMouseClicked(event -> {
                if (this.selectedPane == tempPane) {
                    return;
                }
                if (this.selectedHistoryTextPane != null) {
                    ((Text)this.selectedHistoryTextPane.getChildren().get(0)).setFill(Config.defaultTextColor);
                    this.selectedHistoryTextPane.getChildren().get(0).setStyle("");
                    this.selectedHistoryTextPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                    int currentFieldNumber = Integer.parseInt((
                            (Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().substring(0,
                            ((Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().indexOf(':')));
                    int moveNumber = Integer.parseInt(temp.getText().substring(0, temp.getText().indexOf(':')));
                    if (moveNumber !=  numberMove || currentFieldNumber != numberMove-1) {
                        this.loadPieces(moveNumber);
                    }
                }

                tempPane.setBackground(new Background(new BackgroundFill(Config.selectedMoveHistory, CornerRadii.EMPTY, Insets.EMPTY)));

                ((Text) tempPane.getChildren().get(0)).setFill(Config.selectedTextColor);
                ((Text)tempPane.getChildren().get(0)).setStyle("-fx-font-weight: bold;");
                this.selectedHistoryTextPane = tempPane;
            });

            // if selectedHistoryText is the last move taken or is empty, fire event
            if (this.selectedHistoryTextPane != null &&
                    Integer.parseInt(((Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().substring(0, ((Text)this.selectedHistoryTextPane.getChildren().get(0)).getText().indexOf(':'))) + 1 == numberMove) {
                temp.fireEvent(BoardController.mouseClickedHistoryTextSelected);
            } else if (this.selectedHistoryTextPane == null) {
                temp.fireEvent(BoardController.mouseClickedHistoryTextSelected);
            }

            if ((moveCounter)%2 == 0) {
                RowConstraints row = new RowConstraints(moveHistoryGridPaneHeight);
                row.setMinHeight(moveHistoryGridPaneHeight);
                row.setMaxHeight(moveHistoryGridPaneHeight);
                this.moveHistoryGridPane.getRowConstraints().add(row);
            }
            moveHistoryScrollPane.applyCss();
            moveHistoryScrollPane.layout();

            this.moveHistoryGridPane.add(tempPane, (moveCounter)%2, (moveCounter)/2);
            moveHistoryScrollPane.setVvalue(1.2);
        });
    }

    /**
     * reloads the move history on the right side
     */
    public void reloadMoveHistory() {
        this.selectedHistoryTextPane = null;
        this.moveHistoryGridPane.getChildren().clear();
        this.moveHistoryGridPane.getRowConstraints().clear();
        for (GamestateSnapshot sn : this.gameHandler.getSnapshotHistory()) {
            addMoveToMovelist(sn.getMove(), sn.getMoveCounter());
        }
    }

    /**
     * sets an alert based on the checkmate type
     * @param type: the type of ckeckmate
     * @param whitePlayer: the side that lost
     */
    public void setCheckmateAlert(CHECKMATE_TYPE type, boolean whitePlayer) {
        String text = switch (type) {
            case DRAW -> "The players agreed to a draw";
            case TIME -> String.format("%s ran out of time. %s wins!!!", this.gameHandler.getPlayer(whitePlayer).getName(), this.gameHandler.getPlayer(!whitePlayer).getName());
            case CHECKMATE -> String.format("%s player is checkmate. %s wins!!!", this.gameHandler.getPlayer(whitePlayer).getName(), this.gameHandler.getPlayer(!whitePlayer).getName());
            case STALEMATE -> String.format("%s player can no longer make a legal move in his turn. The game is a stalemate", this.gameHandler.getPlayer(whitePlayer).getName());
            case FIFTY_MOVE_RULE -> String.format("%s claimed a draw with the fifty move rule", this.gameHandler.getPlayer(whitePlayer).getName());
            case THREEFOLD_REPETITION -> "The game is a draw, because of the Threefold Repetition rule.";
        };

        text = text + "\nDo you wish to save the game in the database?";



        if (AlertHandler.showConfirmationAlertAndWait("Game over!!!", text)) {
            this.gameHandler.saveCurrentInDatabase();
        }
    }

    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

}
