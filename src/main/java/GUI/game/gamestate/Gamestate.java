package GUI.game.gamestate;

import GUI.controller.AlertHandler;
import GUI.exceptions.ObjectInterruptedException;
import GUI.game.BoardCoordinate;
import GUI.game.move.Move;
import GUI.game.move.SPECIAL_MOVE;
import GUI.piece.*;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * Represents the chess board (not visual) and handles all calculations.
 */
public class Gamestate {

    private ArrayList<Piece> pieces = new ArrayList<>(); // contains all pieces currently on the board

    // SPECIAL MOVES AND COUNTER VARIABLES

    // true if castle is possible (TRUE IF GENERAL CASTLING IS POSSIBLE. DOES NOT LOOK AT THE BOARD AT ALL)
    private boolean whiteQCastle = true, whiteKCastle = true, blackQCastle = true, blackKCastle = true;
    private BoardCoordinate enPassantCoordinates; // the coordinates of a field that can be taken by en Passant
    private int halfmoveCounter = 0; // half move counts moves since last pawn capture or pawn move
    private int fullmoveCounter = 0; // full move clock counts the total amount of moves
    private boolean whiteTurn;

    private final Semaphore semaphore = new Semaphore(1); // needed as more than one thread could concurrently do something (e.g., makeMove and snapshot at same time)

    /**
     * This constructor is used to fill a new Gamestate with values
     * @param pieces: an ArrayList of all pieces in form of piece objects
     * @param turn: true if it is whites turn, else false
     * @param whiteKCastle: true if king castling is possible for white, else false
     * @param whiteQCastle: true if queen castling is possible for white, else false
     * @param blackKCastle: true if king castling is possible for black, else false
     * @param blackQCastle: true if queen castling is possible for black, else false
     * @param enPassant: A string representing the en-passant position
     * @param halfmoveClock: the current half move counter
     * @param fullmoveClock: the current full move counter
     */
    public Gamestate(
            ArrayList<Piece> pieces,
            boolean turn,
            boolean whiteKCastle,
            boolean whiteQCastle,
            boolean blackKCastle,
            boolean blackQCastle,
            String enPassant,
            int halfmoveClock,
            int fullmoveClock) {

        this.pieces = pieces;
        this.whiteKCastle = whiteKCastle;
        this.whiteQCastle = whiteQCastle;
        this.blackKCastle = blackKCastle;
        this.blackQCastle = blackQCastle;
        this.enPassantCoordinates = new BoardCoordinate(enPassant);
        this.halfmoveCounter = halfmoveClock;
        this.fullmoveCounter = fullmoveClock;
        this.whiteTurn = turn;
    }

    /**
     * This constructor is used to load a GamestateSnapshot into the Gamestate
     * @param snapshot: the snapshot to load
     */
    public Gamestate(GamestateSnapshot snapshot) {
        this.pieces = (ArrayList<Piece>) snapshot.getPieces().stream().map(piece -> {
            switch (piece.getID()) {
                case PAWN -> {
                    return new Pawn((Pawn) piece);
                }
                case ROOK -> {
                    return new Rook((Rook) piece);
                }
                case KNIGHT -> {
                    return new Knight((Knight) piece);
                }
                case BISHOP -> {
                    return new Bishop((Bishop) piece);
                }
                case QUEEN -> {
                    return new Queen((Queen) piece);
                }
                case KING -> {
                    return new King((King) piece);
                }
            }
            return null;
        }).collect(Collectors.toList());
        this.whiteQCastle = snapshot.canWhiteQCastle();
        this.whiteKCastle = snapshot.canWhiteKCastle();
        this.blackQCastle = snapshot.canBlackQCastle();
        this.blackKCastle = snapshot.canBlackKCastle();
        this.enPassantCoordinates = new BoardCoordinate(snapshot.getEnPassantCoordinates());
        this.fullmoveCounter = snapshot.getMove() == null ? snapshot.getFullmoveCounter() : snapshot.getFullmoveCounter()+1;
        this.halfmoveCounter = snapshot.getHalfmoveCounter();
        this.whiteTurn = snapshot.isWhiteTurn();
    }

    /**
     * This function handles everything that is needed to be done, when a move was made.
     * @param move: The move taken
     * @param whiteTurn: the side that made the move (true if white)
     */
    public void makeMove(Move move, boolean whiteTurn) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            AlertHandler.throwError();
            throw new ObjectInterruptedException("The semaphore was unexpectedly interrupted.", e);
        }

        this.whiteTurn = whiteTurn;

        Piece piece;
        if (move.getSpecialMove() == SPECIAL_MOVE.KING_CASTLE) { // move Rook when castling
            if (whiteTurn) {
                this.getPieceAtCoordinates(new BoardCoordinate("H1")).makeMove(new BoardCoordinate("F1"));
                this.getPieceAtCoordinates(new BoardCoordinate("E1")).makeMove(new BoardCoordinate("G1"));
                this.whiteQCastle = false;
                this.whiteKCastle = false;
            } else {
                this.getPieceAtCoordinates(new BoardCoordinate("H8")).makeMove(new BoardCoordinate("F8"));
                this.getPieceAtCoordinates(new BoardCoordinate("E8")).makeMove(new BoardCoordinate("G8"));
                this.blackKCastle = false;
                this.blackQCastle = false;
            }
        } else if (move.getSpecialMove() == SPECIAL_MOVE.QUEEN_CASTLE) { // move rook when castling
            if (whiteTurn) {
                this.getPieceAtCoordinates(new BoardCoordinate("A1")).makeMove(new BoardCoordinate("D1"));
                this.getPieceAtCoordinates(new BoardCoordinate("E1")).makeMove(new BoardCoordinate("C1"));
                this.whiteQCastle = false;
                this.whiteKCastle = false;
            } else {
                this.getPieceAtCoordinates(new BoardCoordinate("A8")).makeMove(new BoardCoordinate("D8"));
                this.getPieceAtCoordinates(new BoardCoordinate("E8")).makeMove(new BoardCoordinate("C8"));
                this.blackKCastle = false;
                this.blackQCastle = false;
            }
        } else {
            piece = this.getPieceAtCoordinates(move.getOldPosition());
            if (piece == null) {
                AlertHandler.throwError();
                throw new IllegalArgumentException("Piece is null in makeMove");
            }
            if (move.isCapture()) { // remove piece if already at end position
                this.removePiece(move.getNewPosition());
            }  else if (move.getSpecialMove() == SPECIAL_MOVE.EN_PASSANT) {
                this.removePiece(new BoardCoordinate(move.getNewPosition().getXLocation(), move.getNewPosition().getYLocation()-1));
            }
            if (piece.getID() == PIECE_ID.ROOK) {
                if (move.getNewPosition().equals(new BoardCoordinate("A1"))) {
                    this.whiteQCastle = false;
                } else if (move.getNewPosition().equals(new BoardCoordinate("A8"))) {
                    this.blackQCastle = false;
                } else if (move.getNewPosition().equals(new BoardCoordinate("H1"))) {
                    this.whiteKCastle = false;
                } else if (move.getNewPosition().equals(new BoardCoordinate("H8"))) {
                    this.blackKCastle = false;
                }
            } else if (piece.getID() == PIECE_ID.KING) {
                if (piece.isWhite()) {
                    this.whiteQCastle = false;
                    this.whiteKCastle = false;
                } else {
                    this.blackKCastle = false;
                    this.blackQCastle = false;
                }
            }
            piece.makeMove(move.getNewPosition()); // make actual move

            if (piece.getID() == PIECE_ID.PAWN || move.isCapture()) {
                this.halfmoveCounter = 0;
            }
        }

        // update full and half move counter
        if (this.whiteTurn) {
            this.fullmoveCounter++;
        }

        // handle special move promotion
        if (move.getSpecialMove() == SPECIAL_MOVE.PROMOTION) {
                this.removePiece(move.getNewPosition());
                this.pieces.add(this.createPromotionPiece(move, whiteTurn));
        }

        //calculate new en Passant possible
        if (move.pawnMoved2Squares()) {
            // check if there are pawns next to the just moved pawn
            Piece leftPiece = this.getPieceAtCoordinates(move.getNewPosition().getLeft());
            leftPiece = leftPiece != null && leftPiece.getID() == PIECE_ID.PAWN ? this.getPieceAtCoordinates(move.getNewPosition().getLeft()) : null;
            Piece rightPiece = this.getPieceAtCoordinates(move.getNewPosition().getRight());
            rightPiece = rightPiece != null && rightPiece.getID() == PIECE_ID.PAWN ? this.getPieceAtCoordinates(move.getNewPosition().getRight()) : null;
            if (leftPiece != null) { // if there is check if it is the correct color (opposite color)
                if (leftPiece.isWhite() == (this.fullmoveCounter % 2 == 1)) {
                    this.enPassantCoordinates = this.fullmoveCounter % 2 == 1 ? move.getNewPosition().getDown() : move.getNewPosition().getUp();
                }
            }
            if (rightPiece != null) {
                if (rightPiece.isWhite() == (this.fullmoveCounter % 2 == 1)) {
                    this.enPassantCoordinates = this.fullmoveCounter % 2 == 1 ? move.getNewPosition().getDown() : move.getNewPosition().getUp();
                }
            }
        } else {
            this.enPassantCoordinates = new BoardCoordinate("-");
        }

        this.semaphore.release();
    }

    /**
     * This function returns a new piece equivalent to the ID of the move.promotion_ID ID.
     * @param move: the move taken
     * @param whiteTurn: the side that took the move
     * @return: Piece object
     */
    private Piece createPromotionPiece(Move move, boolean whiteTurn) {
        Piece promotionPiece = switch (move.getPromotion_ID()) {
            case ROOK -> new Rook(move.getNewPosition(), whiteTurn);
            case KNIGHT -> new Knight(move.getNewPosition(), whiteTurn);
            case BISHOP -> new Bishop(move.getNewPosition(), whiteTurn);
            case QUEEN -> new Queen(move.getNewPosition(), whiteTurn);
            default -> null;
        };
        if (promotionPiece == null) {
            AlertHandler.throwError();
            throw new IllegalArgumentException("promotion piece is null in makeMove");
        }
        return promotionPiece;
    }

    /**
     * Checks if there is a usable piece at the current location. Usable pieces is defined as a piece that
     * can be moved in the current turn. So all white pieces are usable pieces if it is currently white's turn.
     * @param coordinates The coordinates to check
     * @param whiteTurn: The side to check (true if white)
     * @return true if the piece is usable, else false
     */
    public boolean isUsablePiece(BoardCoordinate coordinates, boolean whiteTurn) {
        Piece piece = this.getPieceAtCoordinates(coordinates);
        if (piece == null) { // return false if there is no piece at this coordinates
            return false;
        }

        return piece.isWhite() == whiteTurn;

    }

    /**
     * removes a piece from the piece list.
     * @param coordinates: Coordinates of the piece to remove
     * @return true if piece was removed, else false
     */
    public boolean removePiece(BoardCoordinate coordinates) {
        Piece piece = this.getPieceAtCoordinates(coordinates);
        if (piece != null) {
            this.pieces.remove(piece);
            return true;
        }
        return false;
    }

    /**
     * Creates a snapshot of the current state of the Gamestate object.
     * @param move: the move taken
     * @param whiteClockCounter: the remaining time of the white player
     * @param blackClockCounter: the remaining time of the black player
     * @return GamestateSnapshot instance representing the current Gamestate state
     */
    public GamestateSnapshot getSnapshot(Move move, int whiteClockCounter, int blackClockCounter) {
        return new GamestateSnapshot(
                this.pieces,
                this.whiteTurn,
                this.whiteQCastle,
                this.whiteKCastle,
                this.blackQCastle,
                this.blackKCastle,
                this.enPassantCoordinates,
                this.fullmoveCounter,
                this.halfmoveCounter,
                whiteClockCounter,
                blackClockCounter,
                move);
    }

    /**
     * Creates a snapshot of the current state of the Gamestate object.
     * @param whiteClockCounter: the remaining time of the white player
     * @param blackClockCounter: the remaining time of the black player
     * @return GamestateSnapshot instance representing the current Gamestate state
     */
    public GamestateSnapshot getStartSnapshot (int whiteClockCounter, int blackClockCounter) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            AlertHandler.throwError();
            throw new ObjectInterruptedException("semaphore interrupted unexpectedly", e);
        }

        return new GamestateSnapshot(
                this.pieces,
                !this.whiteTurn,
                this.whiteQCastle,
                this.whiteKCastle,
                this.blackQCastle,
                this.blackKCastle,
                this.enPassantCoordinates,
                this.fullmoveCounter,
                this.halfmoveCounter,
                whiteClockCounter,
                blackClockCounter,
                null);
    }

    /**
     * Creates a snapshot of the current Gamestate. The move will be null.
     * @param whiteClockCounter: the remaining time of the white player
     * @param blackClockCounter: the remaining time of the black player
     * @return GamestateSnapshot instance representing the current Gamestate state
     */
    public GamestateSnapshot getSnapshot(int whiteClockCounter, int blackClockCounter) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            AlertHandler.throwError();
            throw new ObjectInterruptedException("semaphore interrupted unexpectedly", e);
        }

        GamestateSnapshot snapshot = this.getSnapshot(null, whiteClockCounter, blackClockCounter);
        this.semaphore.release();
        return snapshot;
    }

    /**
     * override the equal function of Gamestate to check if 2 gamestates are equal. The equality will be checked,
     * by checking pieces, castling, move counters and en-passant coordinates
     * @param obj: the other gamestate
     * @return: true if the gamestates are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Gamestate other)) {
            return false;
        }
        for (Piece piece : this.pieces) {
            if (!other.pieces.contains(piece)) {
                return false;
            }
        }
        if (!(this.whiteQCastle == other.whiteQCastle)) {
            return false;
        }
        if (!(this.whiteKCastle == other.whiteKCastle)) {
            return false;
        }
        if (!(this.blackQCastle == other.blackQCastle)) {
            return false;
        }
        if (!(this.blackKCastle == other.blackKCastle)) {
            return false;
        }
        if (!this.enPassantCoordinates.equals(other.enPassantCoordinates)) {
            return false;
        }
        if (this.fullmoveCounter != other.fullmoveCounter) {
            return false;
        }
        if (this.halfmoveCounter != other.halfmoveCounter) {
            return false;
        }
        return true;
    }

    public void setPieces(ArrayList<Piece> pieces) {this.pieces = pieces;}

    public ArrayList<Piece> getPieces() {return pieces;}

    public boolean canWhiteQCastle() {return whiteQCastle;}

    public boolean canWhiteKCastle() {return whiteKCastle;}

    public boolean canBlackQCastle() {return blackQCastle;}

    public boolean canBlackKCastle() {return blackKCastle;}

    public BoardCoordinate getEnPassantCoordinates() {return enPassantCoordinates;}

    public int getHalfmoveCounter() {return halfmoveCounter;}

    public int getFullmoveCounter() {return fullmoveCounter;}

    /**
     * BoardCoordinate coordinates: The coordinates you need the piece of
     * Returns the piece at the given coordinates. If there is no piece at this coordinate, it returns null
     * @param coordinates The coordinates to get the piece at
     * @return The piece at the given position, null if there is none
     */
    public Piece getPieceAtCoordinates(BoardCoordinate coordinates) {
        for (Piece piece : pieces) {
            if (piece.getCoordinates().equals(coordinates)) {
                return piece;
            }
        }
        return null;
    }

    public boolean isWhiteTurn() {
        return this.whiteTurn;
    }

    /**
     * returns the current move counter based on the full move counter
     * @return int representing the current move
     */
    public int getMoveCounter() {
        return this.whiteTurn ? this.fullmoveCounter * 2 - 1: this.fullmoveCounter * 2;
    }
}
