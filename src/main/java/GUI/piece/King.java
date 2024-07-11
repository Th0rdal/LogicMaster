package GUI.piece;

import GUI.game.BoardCoordinate;

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

    public King(King king) {
        super(king);
    }

}
