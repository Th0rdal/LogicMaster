package GUI.piece;

import GUI.game.BoardCoordinate;

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

    public Queen(Queen queen) {
        super(queen);
    }

    @Override
    public void makeMove(BoardCoordinate newCoordinates) {
        this.coordinate = newCoordinates;
    }

}
