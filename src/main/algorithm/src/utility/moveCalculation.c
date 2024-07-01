#include "utility/moveCalculation.h"

static inline uint64_t horizontalSlide(const Gamestate* gamestate, const short position);
static inline uint64_t verticalSlide(const Gamestate* gamestate, const short position);
static inline uint64_t diagonalSlide(const Gamestate* gamestate, const short position);
static inline uint64_t antiDiagonalSlide(const Gamestate* gamestate, const short position);
static __attribute__((always_inline)) uint64_t __horizontalSlide(const Gamestate* gamestate, const short position, const bool forWhitePiece);
static __attribute__((always_inline)) uint64_t __verticalSlide(const Gamestate* gamestate, const short position, const bool forWhitePiece);
static __attribute__((always_inline)) uint64_t __diagonalSlide(const Gamestate* gamestate, const short position, const bool forWhitePiece);
static __attribute__((always_inline)) uint64_t __antiDiagonalSlide(const Gamestate* gamestate, const short position, const bool forWhitePiece);
static inline bool canKCastle(const Gamestate* gamestate);
static inline bool canQCastle(const Gamestate* gamestate);

uint64_t createPossibleMovesAfterCheck(const Gamestate* gamestate, const Position* kingPosition, const bool forWhitePiece) {
    uint64_t possibleMovesAfterCheck = 0;
    int side = forWhitePiece ? 1 : 0;
    uint64_t attackingPieces = squareAttacked(gamestate, kingPosition, side);
    uint64_t moves = kingMovesWithSideSelection(gamestate, kingPosition, side);
    short count = __builtin_popcountll(moves);
    Position* kingMoves = getAllPiecePositions(moves, count);
    for (int i = 0; i < count; i++) {
        if (!makeMoveAndCheckLegal(gamestate, KING, kingPosition, (kingMoves+i)))  {
            possibleMovesAfterCheck |= positionDictionary.boards[positionToShort(kingMoves+i)];

        }
    }

    // pawn & knight
    possibleMovesAfterCheck |= attackingPieces & (gamestate->bitboards.color[!side] & (gamestate->bitboards.pawn | gamestate->bitboards.knight));
    
    // rook & queen rank/file moves
    possibleMovesAfterCheck |= (attackingPieces & (rankBitboards.boards[kingPosition->rank-1] ^ rankBitboards.boards[kingPosition->file-1])) & (gamestate->bitboards.color[!side] & (gamestate->bitboards.rook | gamestate->bitboards.queen));

    // bishop & queen diagonal moves
    possibleMovesAfterCheck |= (attackingPieces & (diagonalBitboards.boards[getDiagonalIndexFromPosition(kingPosition)] ^ antiDiagonalBitboards.boards[getAntiDiagonalIndexFromPosition(kingPosition)])) & (gamestate->bitboards.color[!side] & (gamestate->bitboards.bishop | gamestate->bitboards.queen));
    
    return possibleMovesAfterCheck; 
}

bool makeMoveAndCheckLegal(const Gamestate* gamestate, const enum PIECE piece, const Position* piecePosition, const Position* movePosition) {
    Gamestate* newGamestate = gamestateInit();
    int side = !gamestate->flags.isWhiteTurn ? 1 : 0;
    int notSide = gamestate->flags.isWhiteTurn ? 1 : 0;
    short pos = positionToShort(movePosition);
    short originalPos = positionToShort(piecePosition);
    uint64_t originalPosBoard = positionDictionary.boards[originalPos];
    uint64_t notOriginalPosBoard = ~originalPosBoard;
    uint64_t posBoard = positionDictionary.boards[pos];
    uint64_t notPosBoard = ~posBoard;
   
    // bitboards
    newGamestate->bitboards.pawn = gamestate->bitboards.pawn & notOriginalPosBoard;
    newGamestate->bitboards.rook = gamestate->bitboards.rook & notOriginalPosBoard;
    newGamestate->bitboards.knight = gamestate->bitboards.knight & notOriginalPosBoard;
    newGamestate->bitboards.bishop = gamestate->bitboards.bishop & notOriginalPosBoard;
    newGamestate->bitboards.queen = gamestate->bitboards.queen & notOriginalPosBoard;
    newGamestate->bitboards.king = gamestate->bitboards.king & notOriginalPosBoard;
    newGamestate->bitboards.color[side] = gamestate->bitboards.color[side] ^ (originalPosBoard | posBoard);
    newGamestate->bitboards.color[notSide] = gamestate->bitboards.color[notSide] & notPosBoard;
    newGamestate->bitboards.occupancy = newGamestate->bitboards.color[0] | newGamestate->bitboards.color[1];

    switch (piece) {
        case PAWN:
            newGamestate->bitboards.pawn |= posBoard;
            break;
        case ROOK:
            newGamestate->bitboards.rook |= posBoard;
            break;
        case KNIGHT:
            newGamestate->bitboards.knight |= posBoard;
            break;
        case BISHOP:
            newGamestate->bitboards.bishop |= posBoard;
            break;
        case QUEEN:
            newGamestate->bitboards.queen |= posBoard;
            break;
        case KING:
            newGamestate->bitboards.king |= posBoard;
            break;
        default:
           throwError(ERROR_PIECE_NOT_SELECTED, "Error: there was no piece selected to make the Move"); 
    }

    Position* kingPosition = getAllPiecePositions(newGamestate->bitboards.king & newGamestate->bitboards.color[side], 1);
    if (squareAttacked(newGamestate, kingPosition, side)) {
        free(newGamestate);
        return false; 
    }
    free(newGamestate);
    return true;
}

uint64_t squareAttacked(const Gamestate* gamestate, const Position* position, const bool checkForWhite) {
    uint64_t moves = 0;
    uint64_t tempBoard = 0;
    int side = checkForWhite ? 1 : 0;
    int notSide = !checkForWhite ? 1 : 0;
    short pos = positionToShort(position);
    short temp = 0; // used to filter out negative index

    moves |= (__horizontalSlide(gamestate, pos, checkForWhite) | __verticalSlide(gamestate, pos, checkForWhite)) & (gamestate->bitboards.color[notSide] & (gamestate->bitboards.rook | gamestate->bitboards.queen)); 
    moves |= (__diagonalSlide(gamestate, pos, checkForWhite) | __antiDiagonalSlide(gamestate, pos, checkForWhite)) & (gamestate->bitboards.color[notSide] & (gamestate->bitboards.bishop | gamestate->bitboards.queen));
    moves |= knightMoveDictionary.boards[pos] & (gamestate->bitboards.color[notSide] & gamestate->bitboards.knight);

    // pawn calculation. filter negative index and then and with pawns of the other color
    temp = pos+specialMoveDictionary.pawnCaptureLeft[side];
    tempBoard = temp >= 0 ? (positionDictionary.boards[temp] & fileBitboards.reverseBoards[7]) : 0;
    temp = pos+specialMoveDictionary.pawnCaptureRight[side];
    tempBoard |= temp >= 0 ? (positionDictionary.boards[temp] & fileBitboards.reverseBoards[0]) : 0;
    tempBoard &= (gamestate->bitboards.color[notSide] & gamestate->bitboards.pawn);

    moves |= tempBoard;
    return moves;
}

uint64_t pawnMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece) { 
    uint64_t moves = 0;
    uint64_t pos1, pos2, pos3, pos4;
    int side = forWhitePiece ? 1 : 0;
    int notSide = !forWhitePiece ? 1 : 0;

    pos1 = positionDictionary.boards[positionToShort(position)+specialMoveDictionary.pawn1SquareAdd[side]]; // move 1 square
    pos2 = positionDictionary.boards[positionToShort(position)+specialMoveDictionary.pawn2SquareAdd[side]]; //move 2 spaces
    pos3 = positionDictionary.boards[positionToShort(position)+specialMoveDictionary.pawnCaptureRight[side]];
    pos4 = positionDictionary.boards[positionToShort(position)+specialMoveDictionary.pawnCaptureLeft[side]];    
    
    // push 1 square
    moves = pos1 & ~gamestate->bitboards.occupancy;
    // attack
    moves |= ((pos3 & fileBitboards.reverseBoards[0]) | (pos4 & fileBitboards.reverseBoards[7])) & gamestate->bitboards.color[notSide];
    // push 2 squres
    moves |= (moves && position->rank == rankBitboards.pawn2SquareMoveRank[side]) ? (pos2 & ~gamestate->bitboards.occupancy) : 0;
    
    if (gamestate->flags.canEnPassant == true &&
        ((position->rank == gamestate->enPassantPosition.rank-1) || (position->rank == gamestate->enPassantPosition.rank+1))
        && ((position->file == gamestate->enPassantPosition.file-1) || (position->file == gamestate->enPassantPosition.file+1))) {
        
        moves |= positionDictionary.boards[positionToShort(&gamestate->enPassantPosition)]; 
    }    
    return moves;
}

uint64_t rookMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece) {
    uint64_t moves = 0;
    short pos = positionToShort(position);
    moves |= __horizontalSlide(gamestate, pos, forWhitePiece);
    moves |= __verticalSlide(gamestate, pos, forWhitePiece);
    return moves;
}

uint64_t knightMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece) {
    uint64_t moves = knightMoveDictionary.boards[positionToShort(position)];
    moves &= ~(gamestate->bitboards.color[forWhitePiece ? 1 : 0] & moves);
    return moves;
}

uint64_t bishopMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece) {
    uint64_t moves = 0;
    short pos = positionToShort(position);
    moves |= __diagonalSlide(gamestate, pos, forWhitePiece);
    moves |= __antiDiagonalSlide(gamestate, pos, forWhitePiece);
    return moves;
}

uint64_t queenMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece) {
    uint64_t moves = 0;
    short pos = positionToShort(position);
    moves |= __horizontalSlide(gamestate, pos, forWhitePiece);
    moves |= __verticalSlide(gamestate, pos, forWhitePiece);
    moves |= __diagonalSlide(gamestate, pos, forWhitePiece);
    moves |= __antiDiagonalSlide(gamestate, pos, forWhitePiece);
    return moves;
}

uint64_t kingMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece) {
    uint64_t moves = kingMoveDictionary.boards[positionToShort(position)];
    moves &= ~(gamestate->bitboards.color[forWhitePiece ? 1 : 0] & moves); //check if edge pieces are white pieces
    if (forWhitePiece) { //means that the piece MUST be a white piece
        if (canKCastle(gamestate)) {
            moves |= 0x2;
        }
        if (canQCastle(gamestate)) {
            moves |= 0x20;
        }
    } else {
        if (canKCastle(gamestate)) {
            moves |= 0x0200000000000000;
        }
        if (canQCastle(gamestate)) {
            moves |= 0x2000000000000000;
        }
    }
    return moves;
}

/*!
 * check if a king castle can be done
 *
 * @param gamestate: the gamestate to check
 *
 * @return: true if king castle is possible, else false
 *
 * */
static inline bool canKCastle(const Gamestate* gamestate) {
    int side = !gamestate->flags.isWhiteTurn ? 1 : 0;
    return gamestate->flags.kCastle[side]
        && !(gamestate->bitboards.occupancy & specialMoveDictionary.kingCastle[side])
        && (!squareAttacked(gamestate, &specialMoveDictionary.kingCastlePositions[side][0], side) // if squareAttacked is 0, means no piece is attacking this 
        && !squareAttacked(gamestate, &specialMoveDictionary.kingCastlePositions[side][1], side));
}

/*!
 * check if a queen castle can be done
 *
 * @param gamestate: the gamestate to check
 *
 * @return: true if queen castle is possible, else false
 *
 * */
static inline bool canQCastle(const Gamestate* gamestate) {
    int side = !gamestate->flags.isWhiteTurn ? 1 : 0;
    return gamestate->flags.qCastle[side]
        && !(gamestate->bitboards.occupancy & specialMoveDictionary.queenCastle[side])
        && (!squareAttacked(gamestate, &specialMoveDictionary.queenCastlePositions[side][0], side) // if squareAttacked is 0, means no piece is attacking this 
        && !squareAttacked(gamestate, &specialMoveDictionary.queenCastlePositions[side][1], side));
}

/*!
 * Just a middle function when I want to calculate moves in preprocessing where it is always the opposite color of gamestate.
 * Calls the real horizontalSlide function (__horizontalSlide).
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 *
 * @return: bitboard representing all attacks the piece can make in horizontal direction
 * */
static __attribute__((always_inline)) uint64_t horizontalSlide(const Gamestate* gamestate, const short position) {
    return __horizontalSlide(gamestate, position, !gamestate->flags.isWhiteTurn);
}

/*!
 * calculates all possible positions a sliding piece (e.g., rook) can have in horizontal (rank) direction
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 * @param forWhitePiece: the color of the piece (white piece => can capture black pieces)
 *
 * @return: bitboard representing all attacks the piece can make in horizontal direction
 * */
static inline uint64_t __horizontalSlide(const Gamestate* gamestate, const short position, const bool forWhitePiece) {
    uint64_t mask = rankBitboards.boards[position/8]; 
    uint64_t rankOccupancy = gamestate->bitboards.occupancy & mask;
    uint64_t posInBoard = positionDictionary.boards[position];
    uint64_t moves = ((rankOccupancy - (2 * posInBoard)) ^ (reverseBitboard(reverseBitboard(rankOccupancy) - (2 * positionDictionary.boards[63 - position])))) & mask; 
    moves &= ~(gamestate->bitboards.color[forWhitePiece ? 1 : 0] & mask);
    return moves;   
}

/*!
 * Just a middle function when I want to calculate moves in preprocessing where it is always the opposite color of gamestate.
 * Calls the real verticalSlide function (__verticalSlide).
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 *
 * @return: bitboard representing all attacks the piece can make in vertical direction
 * */
static __attribute__((always_inline)) uint64_t verticalSlide(const Gamestate* gamestate, const short position) {
    return __verticalSlide(gamestate, position, !gamestate->flags.isWhiteTurn);
}

/*!
 * calculates all possible positions a sliding piece (e.g., rook) can have in vertical (file) direction
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 * @param forWhitePiece: the color of the piece (white piece => can caputre black pieces)
 *
 * @return: bitboard representing all attacks the piece can make in vertical direction
 * */
static inline uint64_t __verticalSlide(const Gamestate* gamestate, const short position, const bool forWhitePiece) {
    uint64_t mask = fileBitboards.boards[7 - position%8];
    uint64_t fileOccupancy = gamestate->bitboards.occupancy & mask;
    uint64_t posInBoard = positionDictionary.boards[position];
    uint64_t moves = ((fileOccupancy - (2 * posInBoard)) ^ reverseBitboard(reverseBitboard(fileOccupancy) - (2 * positionDictionary.boards[63 - position]))) & mask;
    moves &= ~(gamestate->bitboards.color[forWhitePiece ? 1 : 0] & mask);
    return moves;
}

/*!
 * Just a middle function when I want to calculate moves in preprocessing where it is always the opposite color of gamestate.
 * Calls the real antiDiagonalSlide function (__antiDiagonalSlide).
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 *
 * @return: bitboard representing all attacks the piece can make in the anti diagonal direction
 *
 * OPTIMIZATION: instead of calculating the possible moves based on the diagonal/anti diagonal, I could have a lookup table.
 * bitboard representing all attacks the piece can make in diagonal direction
 *
 * */
static __attribute__((always_inline)) uint64_t antiDiagonalSlide(const Gamestate* gamestate, const short position) {
    return __antiDiagonalSlide(gamestate, position, !gamestate->flags.isWhiteTurn);
}

/*!
 * calculates all possible positions a sliding piece (e.g., bishop) can have in diagonal direction
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 * @param forWhitePiece: the color of the piece (white piece => can caputre black pieces)
 *
 * @return: bitboard representing all attacks the piece can make in the anti diagonal direction
 *
 * OPTIMIZATION: instead of calculating the possible moves based on the diagonal/anti diagonal, I could have a lookup table.
 * bitboard representing all attacks the piece can make in diagonal direction
 * */
static inline uint64_t __antiDiagonalSlide(const Gamestate* gamestate, const short position, const bool forWhitePiece) {
    uint64_t positionInBoard = positionDictionary.boards[position];
    uint64_t antiDiagonal = antiDiagonalBitboards.boards[getAntiDiagonalIndexFromShort(position)];
 
    // calc up left diagonal based from position
    uint64_t upLeftMoves = antiDiagonal & (positionInBoard ^ (-positionInBoard));
    uint64_t blocker = gamestate->bitboards.occupancy & upLeftMoves;
    uint64_t indexFirstAfterBlock = antiDiagonal & (~blocker ^ (blocker - 1));
    uint64_t movesUpLeft = upLeftMoves ^ indexFirstAfterBlock;

    // calc down right diagonal based from position
    uint64_t downRightMoves = antiDiagonal & (~positionInBoard & (positionInBoard - 1));
    blocker = gamestate->bitboards.occupancy & downRightMoves;
    uint64_t temp = blocker == 0 ? 0xFF : positionDictionary.boards[63 -__builtin_clzll(blocker)];
    indexFirstAfterBlock = antiDiagonal & (~temp & (temp - 1));
    uint64_t movesDownRight = downRightMoves ^ indexFirstAfterBlock;

    uint64_t moves = movesUpLeft | movesDownRight;
    moves &= ~(gamestate->bitboards.color[forWhitePiece ? 1 : 0] & antiDiagonal);
    return moves;
}

/*!
 * Just a middle function when I want to calculate moves in preprocessing where it is always the opposite color of gamestate.
 * Calls the real antiDiagonalSlide function (__antiDiagonalSlide).
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 *
 * @return: bitboard representing all attacks the piece can make in diagonal direction
 * */
static __attribute__((always_inline)) uint64_t diagonalSlide(const Gamestate* gamestate, const short position) {
    return __diagonalSlide(gamestate, position, !gamestate->flags.isWhiteTurn); 
}

/*!
 * calculates all possible positions a sliding piece (e.g., bishop) can have in diagonal direction
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 * @param forWhitePiece: the color of the piece (white piece => can caputre black pieces)
 *
 * @return: bitboard representing all attacks the piece can make in diagonal direction
 * */
static inline uint64_t __diagonalSlide(const Gamestate* gamestate, const short position, const bool forWhitePiece) {
    uint64_t positionInBoard = positionDictionary.boards[position];
    uint64_t diagonal = diagonalBitboards.boards[getDiagonalIndexFromShort(position)];
    
    // calc up right diagonal based from position
    uint64_t upRightMoves = diagonal & (positionInBoard ^ (-positionInBoard));
    uint64_t blocker = gamestate->bitboards.occupancy & upRightMoves;
    uint64_t indexFirstAfterBlock = diagonal & (~blocker ^ (blocker - 1));
    uint64_t movesUpRight = upRightMoves ^ indexFirstAfterBlock;

    uint64_t downLeftMoves = diagonal & (~positionInBoard & (positionInBoard - 1));
    blocker = gamestate->bitboards.occupancy & downLeftMoves;
    uint64_t temp = blocker == 0 ? 0xFF : positionDictionary.boards[63 - __builtin_clzll(blocker)];
    indexFirstAfterBlock = diagonal & (~temp & (temp - 1));
    uint64_t movesDownLeft = downLeftMoves ^ indexFirstAfterBlock;
    
    uint64_t moves = movesUpRight | movesDownLeft;
    moves &= ~(gamestate->bitboards.color[forWhitePiece ? 1 : 0] & diagonal);
    return moves;
}
