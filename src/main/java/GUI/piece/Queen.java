package GUI.piece;

import GUI.utilities.BoardCoordinate;
import GUI.utilities.ImageLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class Queen extends Piece{
    /**
     * Represents the Queen Piece
     * @param coordinate The coordinates of the piece
     * @param isWhite true if the piece is white
     */

    public Queen(BoardCoordinate coordinate, boolean isWhite) {
        super(PIECE_ID.QUEEN, coordinate, isWhite);
        if (isWhite) {
           this.path = "/images/white_queen.png";
        } else {
            this.path = "/images/black_queen.png";
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
