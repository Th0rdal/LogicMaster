package GUI.game;

import GUI.piece.PIECE_ID;

import java.util.Objects;

/**
 * Represents a move
 * IDEA: get this from the algorithm and convert it into this form to save it in the DB later on
 */
public class Move {


    private BoardCoordinate oldPosition, newPosition; // board start and end position of the piece


    private PIECE_ID piece_ID; // piece that moved if castling, the king should be the piece_id
    private PIECE_ID promotion_ID; // piece promotion ID
    private boolean capture = false;
    private boolean check = false;
    private boolean checkmate = false;
    private boolean draw = false;
    private boolean ambiguous = false; // different flags
    private SPECIAL_MOVE specialMove; // represents if a special move was made (e.g., en passant)

    public Move(boolean draw) { // if a draw offer is accepted this is used to communicate with the GameHandler
        this.draw = draw;
    }

    public Move(PIECE_ID piece_ID,
                BoardCoordinate oldPosition,
                BoardCoordinate newPosition,
                boolean capture,
                SPECIAL_MOVE specialMove,
                PIECE_ID promotion_ID,
                boolean check,
                boolean checkmate,
                boolean draw,
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

    public Move(Move move) {
        this.piece_ID = move.piece_ID;
        this.oldPosition = move.oldPosition;
        this.newPosition = move.newPosition;
        this.capture = move.capture;
        this.promotion_ID = move.promotion_ID;
        this.check = move.check;
        this.checkmate = move.checkmate;
        this.draw = move.draw;
        this.ambiguous = move.ambiguous;
        this.specialMove = move.specialMove;
    }

    public Move(String moveString) {
        int counter = 0;
        try {
            if (Objects.equals(moveString, "1/2-1/2")) {
                this.draw = true;
            } else if (Objects.equals(moveString, "O-O")) {
                this.specialMove = SPECIAL_MOVE.KING_CASTLE;
                this.oldPosition = new BoardCoordinate("E1");
                this.newPosition = new BoardCoordinate("G1");
            } else if (Objects.equals(moveString, "O-O-O")) {
                this.specialMove = SPECIAL_MOVE.QUEEN_CASTLE;
                this.oldPosition = new BoardCoordinate("E1");
                this.newPosition = new BoardCoordinate("C1");
            } else {
                this.piece_ID = PIECE_ID.fromString(moveString.substring(counter, counter + 1));
                counter = counter + 1;

                this.oldPosition = new BoardCoordinate(moveString.substring(counter, counter + 2));
                counter = counter + 2;

                if (Objects.equals(moveString.substring(counter, counter + 1), "x")) {
                    this.capture = true;
                }
                counter = counter + 1;

                this.newPosition = new BoardCoordinate(moveString.substring(counter, counter + 2));
                counter = counter + 2;

                if (Objects.equals(moveString.substring(counter, counter + 1), "=")) {
                    this.specialMove = SPECIAL_MOVE.PROMOTION;
                    this.promotion_ID = PIECE_ID.fromString(moveString.substring(counter + 1, counter + 2));
                }
                counter = counter + 2;

                if (Objects.equals(moveString.substring(counter, counter + 2), "ep")) {
                    this.specialMove = SPECIAL_MOVE.EN_PASSANT;
                }
                counter = counter + 2;

                if (Objects.equals(moveString.substring(counter, counter + 2), "#")) {
                    this.checkmate = true;
                } else if (Objects.equals(moveString.substring(counter, counter + 2), "+")) {
                    this.check = true;
                }
            }
        } catch (StringIndexOutOfBoundsException e) {}

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

        builder.append(oldPosition.toLowerCaseString());

        if (this.capture) {
            builder.append("x");
        } else {
            builder.append(" ");
        }
        builder.append(newPosition.toLowerCaseString());

        if (this.specialMove == SPECIAL_MOVE.PROMOTION) {
            builder.append("=").append(PIECE_ID.toAbbreviation(this.promotion_ID));
        } else if (this.specialMove == SPECIAL_MOVE.EN_PASSANT) {
            builder.append("ep");
        }

        if (this.checkmate) {
            builder.append("#");
        } else if (this.check) {
            builder.append("+");
        }

        return builder.toString();
    }

    public BoardCoordinate getOldPosition() {
        return oldPosition;
    }

    public BoardCoordinate getNewPosition() {
        return newPosition;
    }

    public PIECE_ID getPiece_ID() {
        return piece_ID;
    }

    public PIECE_ID getPromotion_ID() {
        return promotion_ID;
    }

    public boolean isCapture() {
        return capture;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public boolean isDraw() {
        return draw;
    }

    public boolean isAmbiguous() {
        return ambiguous;
    }

    public SPECIAL_MOVE getSpecialMove() {
        return specialMove;
    }

    public void setPromotionID(PIECE_ID piece) {
        this.promotion_ID = piece;
    }
}
