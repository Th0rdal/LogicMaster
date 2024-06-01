package GUI.piece;

import GUI.utilities.BoardCoordinate;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Represents a piece on the board
 */
public abstract class Piece {

    protected BoardCoordinate coordinate; // coordinates of the piece
    protected String path; // path to the image TODO replace with a new class that loads and returns based on id
    protected PIECE_ID id; // piece ID
    protected boolean isWhite; // true if the piece is white
    private Pane thisPane = null;

    public Piece(PIECE_ID id, BoardCoordinate coordinate, boolean isWhite) {
        this.id = id;
        this.coordinate = coordinate;
        this.isWhite = isWhite;
    }

    public abstract void getMoves(); // get this from algorithm and just return it
    public abstract void makeMove(BoardCoordinate newCoordinates); // change to be a move from a movelist

    /**
     * Prepares the Pane to be added to the board
     * @param imageView:  ImageView representing the image
     * @return Pane wrapped around the ImageView
     */
    protected Pane prepareImage(ImageView imageView) {
        if (this.thisPane != null) {
            return this.thisPane;
        }

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
        this.thisPane = pane;
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

    public Pane getThisPane() {
        return this.thisPane;
    }
}
