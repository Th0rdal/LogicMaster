package GUI.piece;

import GUI.utilities.BoardCoordinate;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Piece {
    /**
     * Represents a Piece on the Board.
     */

    protected BoardCoordinate coordinate;
    protected String path;
    protected PIECE_ID id;
    boolean isWhite;

    public Piece(PIECE_ID id, BoardCoordinate coordinate, boolean isWhite) {
        this.id = id;
        this.coordinate = coordinate;
        this.isWhite = isWhite;
    }
    public abstract void getMoves(); // get this from algorithm and just return it
    public abstract void makeMove(BoardCoordinate newCoordinates); // change to be a move from a movelist

    protected Pane prepareImage(ImageView imageView) {
        /*
         * prepares the Pane to be added to the board
         * @param imageView: ImageView representing the image
         */
        //image config
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        //pane config
        Pane pane = new Pane();
        pane.prefWidthProperty().bind(imageView.fitWidthProperty());
        pane.prefHeightProperty().bind(imageView.fitHeightProperty());
        pane.getChildren().add(imageView);

        imageView.setLayoutX(0);
        imageView.setLayoutY(0);
        return pane;
    }
    public abstract Pane getPieceImage();

    public int getLocationX() {
        return this.coordinate.getXLocation();
    }

    public int getLocationY() {
        return this.coordinate.getYLocation();
    }

    public BoardCoordinate getCoordinates() {
        return this.coordinate;
    }

    public PIECE_ID getID() {
        return this.id;
    }

    public boolean isWhite() {
        return this.isWhite;
    }
}
