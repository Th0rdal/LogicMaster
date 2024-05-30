package GUI.piece;

import GUI.utilities.BoardCoordinate;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;


public class Pawn extends Piece{
    /**
     * Represents the Pawn Piece
     * @param coordinate The coordinates of the piece
     * @param isWhite true if the piece is white
     */

    public Pawn(BoardCoordinate coordinate, boolean isWhite) {
        super(PIECE_ID.PAWN, coordinate, isWhite);
        if (isWhite) {
           this.path = "/images/white_pawn.png";
        } else {
            this.path = "/images/black_pawn.png";
        }
    }

    @Override
    public void getMoves() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    @Override
    public void makeMove(BoardCoordinate newCoordinates) {
        this.coordinate = newCoordinates;
    }

    @Override
    public Pane getPieceImage() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(this.path)));
        return this.prepareImage(new ImageView(image));
    }

}
