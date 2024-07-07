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

    public Piece promote(PIECE_ID id) {
        switch (id) {
            case BISHOP -> {
                return new Bishop(this.coordinate, this.isWhite);
            }
            case KNIGHT -> {
                return new Knight(this.coordinate, this.isWhite);
            }
            case ROOK -> {
                return new Rook(this.coordinate, this.isWhite);
            }
            case QUEEN -> {
                return new Queen(this.coordinate, this.isWhite);
            }
        }
        return null;
    }

}
