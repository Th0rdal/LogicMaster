package GUI.piece;

import GUI.game.BoardCoordinate;
import GUI.utilities.ImageLoader;
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

    public Piece(Piece piece) {
        this.coordinate = piece.coordinate;
        this.path = piece.path;
        this.id = piece.id;
        this.isWhite = piece.isWhite;
        this.thisPane = piece.thisPane;
    }

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

    /**
     * Converts piece abbreviation, color and coordinates into a Piece
     * @param pieceChar: piece abbreviation
     * @param colCounter: counter position of the piece
     * @param rowCounter: row position of the piece
     * @return Piece subclass corresponding to the char piece
     */
    public static Piece createFromChar(char pieceChar, int colCounter, int rowCounter) {
        switch (pieceChar) {
            case 'p':
                return new Pawn(new BoardCoordinate(colCounter, rowCounter), false);
            case 'b':
                return new Bishop(new BoardCoordinate(colCounter, rowCounter), false);
            case 'n':
                return new Knight(new BoardCoordinate(colCounter, rowCounter), false);
            case 'k':
                return new King(new BoardCoordinate(colCounter, rowCounter), false);
            case 'q':
                return new Queen(new BoardCoordinate(colCounter, rowCounter), false);
            case 'r':
                return new Rook(new BoardCoordinate(colCounter, rowCounter), false);
            case 'P':
                return new Pawn(new BoardCoordinate(colCounter, rowCounter), true);
            case 'B':
                return new Bishop(new BoardCoordinate(colCounter, rowCounter), true);
            case 'N':
                return new Knight(new BoardCoordinate(colCounter, rowCounter), true);
            case 'K':
                return new King(new BoardCoordinate(colCounter, rowCounter), true);
            case 'Q':
                return new Queen(new BoardCoordinate(colCounter, rowCounter), true);
            case 'R':
                return new Rook(new BoardCoordinate(colCounter, rowCounter), true);
        }
        String message = "piece value is unexpected (" + pieceChar + ")";
        //TODO exchange with useful message and Error
        throw new RuntimeException(message);
    }

    public Pane getPieceImage() {
        return this.prepareImage(new ImageView(ImageLoader.getImage(this.id, this.isWhite)));
    }

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
