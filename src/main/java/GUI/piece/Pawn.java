package GUI.piece;

import GUI.game.BoardCoordinate;


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

    public Pawn(Pawn pawn) {
        super(pawn);
    }

    @Override
    public void makeMove(BoardCoordinate newCoordinates) {
        this.coordinate = newCoordinates;
    }

}
