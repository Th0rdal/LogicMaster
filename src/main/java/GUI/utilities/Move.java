package GUI.utilities;

import GUI.piece.PIECE_ID;

/**
 * Represents a move
 * IDEA: get this from the algorithm and convert it into this form to save it in the DB later on
 */
public class Move {


    private BoardCoordinate oldPosition, newPosition; // board start and end position of the piece


    private PIECE_ID piece_ID; // piece that moved if castling, the king should be the piece_id
    private PIECE_ID promotion_ID; // piece promotion ID
    private boolean capture, check, checkmate, ambiguous; // different flags
    private SPECIAL_MOVE specialMove; // represents if a special move was made (e.g., en passant)


    public Move(PIECE_ID piece_ID,
                BoardCoordinate oldPosition,
                BoardCoordinate newPosition,
                boolean capture,
                SPECIAL_MOVE specialMove,
                PIECE_ID promotion_ID,
                boolean check,
                boolean checkmate,
                boolean ambiguous) {

        this.piece_ID = piece_ID;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.capture = capture;
        this.promotion_ID = promotion_ID;
        this.check = check;
        this.checkmate = checkmate;
        this.ambiguous = ambiguous;
        this.specialMove = specialMove;
    }

    @Override
    public String toString() {
        if (this.specialMove == SPECIAL_MOVE.QUEEN_CASTLE) {
            return "O-O-O";
        } else if (this.specialMove == SPECIAL_MOVE.KING_CASTLE) {
            return "O-O";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(PIECE_ID.toAbbreviation(this.piece_ID));
        if (this.ambiguous) {
            builder.append(oldPosition.toLowerCaseString());
        }
        if (this.capture) {
            builder.append("x");
        }
        builder.append(newPosition.toLowerCaseString());

        if (this.specialMove == SPECIAL_MOVE.PROMOTION) {
            builder.append("=").append(PIECE_ID.toAbbreviation(this.promotion_ID));
        } else if (this.specialMove == SPECIAL_MOVE.EN_PASSANT) {
            builder.append(" e.p.");
        }

        if (this.checkmate) {
            builder.append("#");
        } else if (this.check) {
            builder.append("+");
        }

        return builder.toString();
    }
}
