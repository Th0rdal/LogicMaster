package GUI.gamestate;

import GUI.piece.*;
import GUI.utilities.*;

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

    private Semaphore semaphore = new Semaphore(1); // needed as more than one thread could concurrently do something (e.g., makeMove and snapshot at same time)

    /**
     * empty constructor if just a Gamestate should be created.
     * NEEDS TO BE LOADED LATER ON USING BoardConverter.loadFEN
     */
    public Gamestate() {}

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
        this.whiteQCastle = snapshot.isWhiteQCastle();
        this.whiteKCastle = snapshot.isWhiteKCastle();
        this.blackQCastle = snapshot.isBlackQCastle();
        this.blackKCastle = snapshot.isBlackKCastle();
        this.enPassantCoordinates = new BoardCoordinate(enPassantCoordinates);
        this.fullmoveCounter = snapshot.getFullmoveCounter();
        this.halfmoveCounter = snapshot.getHalfmoveCounter();
    }

    /**
     * This function handles everything that is needed to be done, when a move was made.
     * It takes the clockCounters, because they are needed for GamestateSnapshot creation.
     * @param move: The move taken
     * @param whiteClockCounter: the current time of the white player
     * @param blackClockCounter: the current time of the black player
     */
    public GamestateSnapshot makeMove(Move move, int whiteClockCounter, int blackClockCounter) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean whiteTurn = this.fullmoveCounter+1 % 2 == 1;

        Piece piece = this.getPieceAtCoordinates(move.getOldPosition());
        if (piece == null) {
            //TODO make this a useful Error
            throw new RuntimeException("UNEXPECTED. THIS SHOULD NEVER HAPPEN. piece is null in makeMove");
        }
        if (move.isCapture()) { // remove piece if already at end position
            this.removePiece(move.getNewPosition());
        } else if (move.getSpecialMove() == SPECIAL_MOVE.KING_CASTLE) { // move Rook when castling
            if (whiteTurn) {
                this.getPieceAtCoordinates(new BoardCoordinate("H1")).makeMove(new BoardCoordinate("F1"));
            } else {
                this.getPieceAtCoordinates(new BoardCoordinate("H8")).makeMove(new BoardCoordinate("F8"));
            }
        } else if (move.getSpecialMove() == SPECIAL_MOVE.QUEEN_CASTLE) { // move rook when castling
            if (whiteTurn) {
                this.getPieceAtCoordinates(new BoardCoordinate("A1")).makeMove(new BoardCoordinate("D1"));
            } else {
                this.getPieceAtCoordinates(new BoardCoordinate("A8")).makeMove(new BoardCoordinate("D8"));
            }
        } else if (move.getSpecialMove() == SPECIAL_MOVE.EN_PASSANT) {
            this.removePiece(new BoardCoordinate(move.getNewPosition().getXLocation(), move.getNewPosition().getYLocation()-1));
        }

        piece.makeMove(move.getNewPosition()); // make actual move

        // update full and half move counter
        this.fullmoveCounter++;
        if (piece.getID() == PIECE_ID.PAWN || move.isCapture()) {
            this.halfmoveCounter = 0;
        } else {
            this.halfmoveCounter++;
        }

        // handle special move promotion
        if (move.getSpecialMove() == SPECIAL_MOVE.PROMOTION) {
                this.removePiece(move.getNewPosition());
                this.pieces.add(this.createPromotionPiece(move, whiteTurn));
        }

        GamestateSnapshot snapshot =  this.saveSnapshot(move, whiteClockCounter, blackClockCounter);
        this.semaphore.release();
        return snapshot;
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
            throw new RuntimeException("UNEXPECTED. PROMOTION IS NULL in makeMove");
        }
        return promotionPiece;
    }

    /**
     * Checks if the there is a piece on the given coordinates.
     * @param coordinates The coordinates to check
     * @return true if there is a piece, else false
     */
    public boolean hasPiece(BoardCoordinate coordinates) {
        // TODO check if needed
        Piece piece = this.getPieceAtCoordinates(coordinates);
        if (piece == null) { // return false if there is no piece at this coordinates
            return false;
        }
        return true;
    }

    /**
     * Checks if there is a usable piece at the current location. Usable pieces is defined as a piece that
     * can be moved in the current turn. So all white pieces are usable pieces if it is currently white's turn.
     * @param coordinates The coordinates to check
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
     * loads a fresh new default board and resets all values needed: This is equivalent to the FEN notation
     * that is saved in the variable START_POSITION
     */
    public void loadStartPosition() {
        // TODO check if this is useful
        // prob not, because a new Gamestate can just be created

        // add all black pieces
        for (int i = 1; i <= 8; i++) {
            pieces.add(new Pawn(new BoardCoordinate(i, 7), false));
        }
        pieces.add(new Rook(new BoardCoordinate(1, 8), false));
        pieces.add(new Rook(new BoardCoordinate(8, 8), false));
        pieces.add(new King(new BoardCoordinate(5, 8), false));
        pieces.add(new Queen(new BoardCoordinate(4, 8), false));
        pieces.add(new Knight(new BoardCoordinate(2, 8), false));
        pieces.add(new Knight(new BoardCoordinate(7, 8), false));
        pieces.add(new Bishop(new BoardCoordinate(3, 8), false));
        pieces.add(new Bishop(new BoardCoordinate(6, 8), false));

        // add all white pieces
        for (int i = 1; i <= 8; i++) {
            pieces.add(new Pawn(new BoardCoordinate(i, 2), true));
        }
        pieces.add(new Rook(new BoardCoordinate(1, 1), true));
        pieces.add(new Rook(new BoardCoordinate(8, 1), true));
        pieces.add(new King(new BoardCoordinate(5, 1), true));
        pieces.add(new Queen(new BoardCoordinate(4, 1), true));
        pieces.add(new Knight(new BoardCoordinate(2, 1), true));
        pieces.add(new Knight(new BoardCoordinate(7, 1), true));
        pieces.add(new Bishop(new BoardCoordinate(3, 1), true));
        pieces.add(new Bishop(new BoardCoordinate(6, 1), true));

        // all Castling possible
        this.whiteQCastle = true;
        this.whiteKCastle = true;
        this.blackQCastle = true;
        this.blackKCastle = true;
        this.enPassantCoordinates = null;
        this.halfmoveCounter = 0;
        this.fullmoveCounter = 0;
        this.enPassantCoordinates = new BoardCoordinate("-");
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
    private GamestateSnapshot saveSnapshot(Move move, int whiteClockCounter, int blackClockCounter) {
        return new GamestateSnapshot(
                this.pieces,
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
     * Creates a snapshot of the current Gamestate. The move will be null.
     * @param whiteClockCounter: the remaining time of the white player
     * @param blackClockCounter: the remaining time of the black player
     * @return GamestateSnapshot instance representing the current Gamestate state
     */
    public GamestateSnapshot getCurrentSnapshot(int whiteClockCounter, int blackClockCounter) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        GamestateSnapshot snapshot = this.saveSnapshot(null, whiteClockCounter, blackClockCounter);
        this.semaphore.release();
        return snapshot;
    }

    /**
     * Loads the given configuration into the Gamestate
     * TODO check if this should not just be a constructor
     * @param pieces
     * @param turn
     * @param whiteKCastle
     * @param whiteQCastle
     * @param blackKCastle
     * @param blackQCastle
     * @param enPassant
     * @param halfmoveClock
     * @param fullmoveClock
     */
    public void loadConfiguration(
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

}
