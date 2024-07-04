package GUI.piece;

import GUI.utilities.BoardCoordinate;
import GUI.utilities.ImageLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class Bishop extends Piece{
    /**
     * Represents the Bishop Piece
     * @param coordinate The coordinates of the piece
     * @param isWhite true if the piece is white
     */

    public Bishop(BoardCoordinate coordinate, boolean isWhite) {
        super(PIECE_ID.BISHOP, coordinate, isWhite);
        if (isWhite) {
           this.path = "/images/white_bishop.png";
        } else {
            this.path = "/images/black_bishop.png";
        }
    }

    public Bishop(Bishop bishop) {
        super(bishop);
    }

    @Override
    public void makeMove(BoardCoordinate newCoordinates) {
        this.coordinate = newCoordinates;
    }

}
