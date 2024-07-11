package GUI.game.move;

import GUI.controller.AlertHandler;
import GUI.game.BoardCoordinate;
import GUI.piece.PIECE_ID;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a move on the board
 */
public class Move {


    private BoardCoordinate oldPosition, newPosition; // board start and end position of the piece


    private PIECE_ID piece_ID; // piece that moved if castling, the king should be the piece_id
    private PIECE_ID promotion_ID; // piece promotion ID
    private boolean drawOffered = false;
    private boolean capture = false;
    private boolean check = false;
    private boolean checkmate = false;
    private boolean draw = false;
    private SPECIAL_MOVE specialMove; // represents if a special move was made (e.g., en passant)

    /**
     * only use this constructor if the move was a draw by agreement. It is used to communicate between controller and gameHandler
     * @param draw: if it was a draw
     */
    public Move(boolean draw) { // if a draw offer is accepted this is used to communicate with the GameHandler
        this.draw = draw;
        this.drawOffered = true;
    }

    /**
     * creates move based on all variables
     * @param piece_ID: the id of the piece that made the move
     * @param oldPosition: the position the piece was before the move was made
     * @param newPosition: the position the piece was after the move was made
     * @param capture: true if a piece was captured
     * @param specialMove: the special move if one was made
     * @param promotion_ID: the id of the piece that the given piece gets promoted to
     * @param check: true if the piece made a check
     * @param checkmate: true if the move checkmated the opponent
     * @param draw: true if the game was a draw
     */
    public Move(PIECE_ID piece_ID,
                BoardCoordinate oldPosition,
                BoardCoordinate newPosition,
                boolean capture,
                SPECIAL_MOVE specialMove,
                PIECE_ID promotion_ID,
                boolean check,
                boolean checkmate,
                boolean draw) {

        this.piece_ID = piece_ID;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.capture = capture;
        this.promotion_ID = promotion_ID;
        this.check = check;
        this.checkmate = checkmate;
        this.specialMove = specialMove;
        this.draw = draw;
    }

    /**
     * create a move out of its byte representation
     * @param base
     * @param base2
     * @param extended
     */
    public Move(byte base, byte base2, byte extended) {
        // base ->  3 bit piece, capture, check, checkmate, draw, none
        // base2 -> 3 bit posX end, 3 bit poxY end, promotion, en passant
        // extended -> 3 bit posX start, 3 bit posY start, none, none

        int pieceID = ((base & 224) >> 5);
        this.piece_ID = convertByteToPieceID(pieceID);
        this.capture = ((base & 16) >> 4) == 1;
        this.check = ((base & 8) >> 3) == 1;
        this.checkmate = ((base & 4) >> 2) == 1;
        this.draw = ((base & 2) >> 1) == 1;

        int endX = ((base2 & 224) >> 5) + 1;
        int endY = ((base2 & 28) >> 2) + 1;
        this.newPosition = new BoardCoordinate(endX, endY);

        int startX = ((extended & 224) >> 5) + 1;
        int startY = ((extended & 28) >> 2) + 1;
        this.oldPosition = new BoardCoordinate(startX, startY);

        if (((base2 & 2) >> 1) == 1) {
            this.specialMove = SPECIAL_MOVE.PROMOTION;
            this.promotion_ID = piece_ID;
            this.piece_ID = PIECE_ID.PAWN;
        } else if ((base2 & 1) == 1) {
            this.specialMove = SPECIAL_MOVE.EN_PASSANT;
        } else if (pieceID == 6) {
            this.specialMove = SPECIAL_MOVE.KING_CASTLE;
        } else if (pieceID == 7) {
            this.specialMove = SPECIAL_MOVE.QUEEN_CASTLE;
        } else {
            this.specialMove = SPECIAL_MOVE.NONE;
        }

    }

    /**
     * copy constructor
     * @param move: move to copy
     */
    public Move(Move move) {
        this.piece_ID = move.piece_ID;
        this.oldPosition = move.oldPosition;
        this.newPosition = move.newPosition;
        this.capture = move.capture;
        this.promotion_ID = move.promotion_ID;
        this.check = move.check;
        this.checkmate = move.checkmate;
        this.draw = move.draw;
        this.specialMove = move.specialMove;
        this.drawOffered = move.drawOffered;
    }

    /**
     * creates move based on full notation string
     * @param moveString: the full notation string
     */
    public Move(String moveString) {
        int counter = 0;
        try {
            if (Objects.equals(moveString, "1/2-1/2")) {
                this.draw = true;
            } else if (Objects.equals(moveString, "O-O")) {
                this.specialMove = SPECIAL_MOVE.KING_CASTLE;
                this.oldPosition = new BoardCoordinate("-");
                this.newPosition = new BoardCoordinate("-");
                this.piece_ID = PIECE_ID.KING;
            } else if (Objects.equals(moveString, "O-O-O")) {
                this.specialMove = SPECIAL_MOVE.QUEEN_CASTLE;
                this.oldPosition = new BoardCoordinate("-");
                this.newPosition = new BoardCoordinate("-");
                this.piece_ID = PIECE_ID.KING;
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
        } catch (StringIndexOutOfBoundsException ignored) {}

    }

    /**
     * overwrites the toString function of move
     * @return: full notation string representation of the move
     */
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

    /**
     * checks if the pawn moved 2 squares
     * @return: true if the pawn moved 2 squares
     */
    public boolean pawnMoved2Squares() {
        return this.piece_ID == PIECE_ID.PAWN && Math.abs(this.oldPosition.getYLocation() - this.newPosition.getYLocation()) == 2;
    }

    /**
     * converts a byte array into an ArrayList of representing moves
     * @param byteMoves: the byte array of moves
     * @return: ArrayList of moves after converting them from the bytes
     */
    public static ArrayList<Move> convertByteMovesToArrayList(byte[] byteMoves) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < byteMoves.length; i=i+3) {
             moves.add(new Move(byteMoves[i], byteMoves[i+1], byteMoves[i+2]));
        }
        return moves;
    }

    /**
     * convert the move into byte representation
     * @return: the move as byte array representation (always containing 3 bytes)
     */
    public byte[] convertToByte() {
        // base ->  3 bit piece, capture, check, checkmate, draw, none
        // base2 -> 3 bit posX end, 3 bit poxY end, promotion, en passant
        // extended -> 3 bit posX start, 3 bit posY start, none, none
        byte[] moveBytes = new byte[3];

        moveBytes[0] |= (byte) (this.convertPieceIDToByte() << 5);
        moveBytes[0] |= (byte) ((byte) (this.capture ? 1 : 0) << 4);
        moveBytes[0] |= (byte) ((byte) (this.check ? 1 : 0) << 3);
        moveBytes[0] |= (byte) ((byte) (this.checkmate ? 1 : 0) << 2);
        moveBytes[0] |= (byte) ((byte) (this.draw ? 1 : 0) << 1);

        moveBytes[1] |= (byte) (this.newPosition.convertToByte() << 2);
        moveBytes[1] |= (byte) ((byte) (this.specialMove == SPECIAL_MOVE.PROMOTION ? 1 : 0) << 1);
        moveBytes[1] |= (byte) ((byte) (this.specialMove == SPECIAL_MOVE.EN_PASSANT ? 1 : 0));

        moveBytes[2] |= (byte) ((byte) (this.oldPosition.convertToByte() << 2));

        return moveBytes;
    }

    /**
     * converts a byte representing the piece id to the PIECE_ID enum
     * @param pieceByte: byte representation of the piece ID
     * @return
     */
    private PIECE_ID convertByteToPieceID(int pieceByte) {
        return switch (pieceByte) {
            case 0 -> PIECE_ID.PAWN;
            case 1 -> PIECE_ID.ROOK;
            case 2 -> PIECE_ID.KNIGHT;
            case 3 -> PIECE_ID.BISHOP;
            case 4 -> PIECE_ID.QUEEN;
            case 5, 6, 7 -> PIECE_ID.KING;
            default -> {
                AlertHandler.throwError();
                throw new IllegalArgumentException(MessageFormat.format("The byte ({0}) not between 0 and 7", pieceByte));
            }
        };
    }

    /**
     * converts a piece id into its byte representation
     * @return: the piece id in byte representation
     */
    private byte convertPieceIDToByte() {
        if (this.specialMove == SPECIAL_MOVE.KING_CASTLE) {
            return (byte) 6;
        } else if (this.specialMove == SPECIAL_MOVE.QUEEN_CASTLE) {
            return (byte) 7;
        }
        return (byte) switch (this.piece_ID) {
            case PAWN -> 0;
            case ROOK -> 1;
            case KNIGHT -> 2;
            case BISHOP -> 3;
            case QUEEN -> 4;
            case KING -> 5;
        };
    }

    /**
     * checks if this move is a castling move.
     * @param coordinate: the end coordinates of the piece (needed, because if the move is a castle move, start and end coordinates are empty)
     * @return
     */
    public boolean isCastlingMove(BoardCoordinate coordinate) {
        if (this.specialMove == SPECIAL_MOVE.KING_CASTLE
                && coordinate.equals(new BoardCoordinate("G1"))
                && this.piece_ID == PIECE_ID.KING) {

            return true;
        } else if (this.specialMove == SPECIAL_MOVE.KING_CASTLE
                && coordinate.equals(new BoardCoordinate("G8"))
                && this.piece_ID == PIECE_ID.KING) {

            return true;
        }else if (this.specialMove == SPECIAL_MOVE.QUEEN_CASTLE
                && coordinate.equals(new BoardCoordinate("C1"))
                && this.piece_ID == PIECE_ID.KING) {

            return true;
        } else if (this.specialMove == SPECIAL_MOVE.QUEEN_CASTLE
                && coordinate.equals(new BoardCoordinate("C8"))
                && this.piece_ID == PIECE_ID.KING) {

            return true;
        }
        return false;
    }

    public BoardCoordinate getOldPosition() {
        return oldPosition;
    }

    public BoardCoordinate getNewPosition() {
        return newPosition;
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

    public boolean isDrawOffered() {return this.drawOffered;}

    public SPECIAL_MOVE getSpecialMove() {
        return specialMove;
    }

}
