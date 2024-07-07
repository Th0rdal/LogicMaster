package GUI.piece;

import GUI.game.BoardCoordinate;

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

    public Knight(Knight knight) {
        super(knight);
    }

    @Override
    public void makeMove(BoardCoordinate newCoordinates) {
        this.coordinate = newCoordinates;
    }

}
