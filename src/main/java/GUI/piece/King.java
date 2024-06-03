package GUI.piece;

import GUI.utilities.BoardCoordinate;
import GUI.utilities.ImageLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class King extends Piece{
    /**
     * Represents the King Piece
     * @param coordinate The coordinates of the piece
     * @param isWhite true if the piece is white
     */

    public King(BoardCoordinate coordinate, boolean isWhite) {
        super(PIECE_ID.KING, coordinate, isWhite);
        if (isWhite) {
           this.path = "/images/white_king.png";
        } else {
            this.path = "/images/black_king.png";
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
}
