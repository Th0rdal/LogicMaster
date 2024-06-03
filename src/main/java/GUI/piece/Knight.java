package GUI.piece;

import GUI.utilities.BoardCoordinate;
import GUI.utilities.ImageLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class Knight extends Piece{
    /**
     * Represents the Knight Piece
     * @param coordinate The coordinates of the piece
     * @param isWhite true if the piece is white
     */

    public Knight(BoardCoordinate coordinate, boolean isWhite) {
        super(PIECE_ID.KNIGHT, coordinate, isWhite);
        if (isWhite) {
           this.path = "/images/white_knight.png";
        } else {
            this.path = "/images/black_knight.png";
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
        return this.prepareImage(new ImageView(ImageLoader.getImage(this.id, this.isWhite)));
    }
}
