#include "minimax/moveCalculation.h"

/**
 * calculates all possible positions a sliding piece (e.g., rook) can have in horizontal (rank) direction
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 *
 * @return: bitboard representing all attacks the piece can make in horizontal direction
 * */
uint64_t horizontailSlide(Gamestate* gamestate, short position) {
    uint64_t mask = rankBitboards.boards[position/8]; 
    uint64_t rankOccupancy = gamestate->bitboards.occupancy & mask;
    uint64_t posInBoard = 1 << position;
    uint64_t moves = (rankOccupancy - (2 * posInBoard) ^ reverseBitboard(reverseBitboard(rankOccupancy) - (2 * reverseBitboard(posInBoard)))) & mask;
    if (gamestate->flags.isWhiteTurn) { //means that the piece MUST be a white piece
        moves = moves ^ (gamestate->bitboards.white & mask); //check if edge pieces are white pieces
    } else {
        moves = moves ^ (gamestate->bitboards.black & mask);
    }
    return moves;
}

/*
 * calculates all possible positions a sliding piece (e.g., rook) can have in vertical (file) direction
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 *
 * @return: bitboard representing all attacks the piece can make in vertical direction
 * */
uint64_t verticalSlide(Gamestate* gamestate, short position) {
    uint64_t mask = fileBitboards.boards[position%8];
    uint64_t fileOccupancy = gamestate->bitboards.occupancy & mask;
    uint64_t posInBoard = 1 << position;
    uint64_t moves = ((fileOccupancy - (2 * posInBoard)) ^ reverseBitboard(reverseBitboard(fileOccupancy) - (2 * reverseBitboard(posInBoard)))) & mask;
    if (gamestate->flags.isWhiteTurn) {
        moves = moves ^ (gamestate->bitboards.white & mask);
    } else {
        moves = moves ^ (gamestate->bitboards.black & mask);
    }
    return moves;
}

// OPTIMIZATION: instead of calculating the possible moves based on the diagonal/anti diagonal, I could have a lookup table.
uint64_t antiDiagonalSlide(Gamestate* gamestate, short position) {
    uint64_t positionInBoard = 1 << position;
    uint64_t antiDiagonal = antiDiagonalBitboards.boards[getAntiDiagonalIndexFromShort(position)];
 
    // calc up left diagonal based from position
    uint64_t upLeftMoves = antiDiagonal & ~(positionInBoard ^ (positionInBoard - 1));
    uint64_t blocker = gamestate->bitboards.occupancy & upLeftMoves;
    uint64_t indexFirstAfterBlock = antiDiagonal & ~(blocker ^ (blocker - 1));
    uint64_t movesUpLeft = upLeftMoves ^ indexFirstAfterBlock;

    // calc down right diagonal based from position
    uint64_t downRightMoves = antiDiagonal & (positionInBoard ^ (positionInBoard - 1));
    blocker = gamestate->bitboards.occupancy & downRightMoves;
    short temp = __builtin_clzll(blocker);
    indexFirstAfterBlock = antiDiagonal & (temp ^ (temp - 1));
    uint64_t movesDownRight = downRightMoves ^ indexFirstAfterBlock;
    
    uint64_t moves = movesUpLeft | movesDownRight;
    if (gamestate->flags.isWhiteTurn) {
        moves = moves ^ (moves & gamestate->bitboards.white & antiDiagonal);
    } else {
        moves = moves ^ (moves & gamestate->bitboards.black & antiDiagonal);
    }

    return moves;
}

uint64_t diagonalSlide(Gamestate* gamestate, short position) {
    uint64_t positionInBoard = 1 << position;
    uint64_t diagonal = diagonalBitboards.boards[getDiagonalIndexFromShort(position)];

    // calc up right diagonal based from position
    uint64_t upRightMoves = diagonal & (positionInBoard ^ (positionInBoard - 1));
    uint64_t blocker = gamestate->bitboards.occupancy & upRightMoves;
    uint64_t indexFirstAfterBlock = diagonal & ~(blocker ^ (blocker - 1));
    uint64_t movesUpRight = upRightMoves ^ indexFirstAfterBlock;

    uint64_t downLeftMoves = diagonal & (positionInBoard ^ (positionInBoard - 1));
    blocker = gamestate->bitboards.occupancy & downLeftMoves;
    short temp = __builtin_clzll(blocker);
    indexFirstAfterBlock = diagonal & (temp ^ (temp - 1));
    uint64_t movesDownLeft = downLeftMoves ^ indexFirstAfterBlock;

    uint64_t moves = movesUpRight | movesDownLeft;
    if (gamestate->flags.isWhiteTurn) {
        moves = moves ^ (moves & gamestate->bitboards.white & diagonal);
    } else {
        moves = moves ^ (moves & gamestate->bitboards.black & diagonal);
    }

    return moves;
}
