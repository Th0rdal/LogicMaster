package GUI.piece;

import GUI.game.BoardCoordinate;

public class Rook extends Piece{
    /**
     * Represents the Rook Piece
     * @param coordinate The coordinates of the piece
     * @param isWhite true if the piece is white
     */

    public Rook(BoardCoordinate coordinate, boolean isWhite) {
        super(PIECE_ID.ROOK, coordinate, isWhite);
        if (isWhite) {
           this.path = "/images/white_rook.png";
        } else {
            this.path = "/images/black_rook.png";
        }
    }

    public Rook(Rook rook) {
        super(rook);
    }

    @Override
    public void makeMove(BoardCoordinate newCoordinates) {
        this.coordinate = newCoordinates;
    }

}
