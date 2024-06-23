#include "utility/moveCalculation.h"

static uint64_t horizontalSlide(Gamestate* gamestate, short position);
static uint64_t verticalSlide(Gamestate* gamestate, short position);
static uint64_t diagonalSlide(Gamestate* gamestate, short position);
static uint64_t antiDiagonalSlide(Gamestate* gamestate, short position);
static bool canKCastle(Gamestate* gamestate);
static bool canQCastle(Gamestate* gamestate);
uint64_t squareAttacked(Gamestate* gamestate, Position* position);

/**
 * calculates all possible moves a given pawn can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the pawn as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t pawnMoves(Gamestate* gamestate, Position* position) { 
    uint64_t moves = 0;
    uint64_t pos1, pos2, pos3, pos4;
    bool side = gamestate->flags.isWhiteTurn;

    pos1 = positionDictionary.boards[positionToShort(position)+specialMoveDictionary.pawn1SquareAdd[side]]; // move 1 square
    pos2 = positionDictionary.boards[positionToShort(position)+specialMoveDictionary.pawn2SquareAdd[side]]; //move 2 spaces
    pos3 = positionDictionary.boards[positionToShort(position)+specialMoveDictionary.pawnCaptureRight[side]];
    pos4 = positionDictionary.boards[positionToShort(position)+specialMoveDictionary.pawnCaptureLeft[side]];    
    
    //push
    moves = pos1 & ~gamestate->bitboards.occupancy;
    moves |= ((pos3 & fileBitboards.reverseBoards[0]) | (pos4 & fileBitboards.reverseBoards[7])) & gamestate->bitboards.color[!side];
    moves |= (moves && position->rank == rankBitboards.pawn2SquareMoveRank[side]) ? (pos2 & ~gamestate->bitboards.occupancy) : 0;
    
    if (gamestate->flags.canEnPassant == true &&
        ((position->rank == gamestate->enPassantPosition.rank-1) || (position->rank == gamestate->enPassantPosition.rank+1))
        && ((position->file == gamestate->enPassantPosition.file-1) || (position->file == gamestate->enPassantPosition.file+1))) {
        
        moves |= positionDictionary.boards[positionToShort(&gamestate->enPassantPosition)]; 
    }    
    return moves;
}

/**
 * calculates all possible moves a given rook can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the rook as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t rookMoves(Gamestate* gamestate, Position* position) {
    uint64_t moves = 0;
    short pos = positionToShort(position);
    moves |= horizontalSlide(gamestate, pos);
    moves |= verticalSlide(gamestate, pos);
    return moves;
}

/**
 * calculates all possible moves a given knight can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the knight as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t knightMoves(Gamestate* gamestate, Position* position) {
    uint64_t moves = knightMoveDictionary.boards[positionToShort(position)];
    moves &= ~(gamestate->bitboards.color[gamestate->flags.isWhiteTurn] & moves);
    return moves;
}

/**
 * calculates all possible moves a given bishop can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the bishop as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t bishopMoves(Gamestate* gamestate, Position* position) {
    uint64_t moves = 0;
    short pos = positionToShort(position);
    moves |= diagonalSlide(gamestate, pos);
    moves |= antiDiagonalSlide(gamestate, pos);
    return moves;
}

/**
 * calculates all possible moves a given queen can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the queen as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t queenMoves(Gamestate* gamestate, Position* position) {
    uint64_t moves = 0;
    short pos = positionToShort(position);
    moves |= horizontalSlide(gamestate, pos);
    moves |= verticalSlide(gamestate, pos);
    moves |= diagonalSlide(gamestate, pos);
    moves |= antiDiagonalSlide(gamestate, pos);
    return moves;
}

/**
 * calculates all possible moves a given king can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the king as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t kingMoves(Gamestate* gamestate, Position* position) {
    uint64_t moves = kingMoveDictionary.boards[positionToShort(position)];
    moves &= ~(gamestate->bitboards.color[gamestate->flags.isWhiteTurn] & moves); //check if edge pieces are white pieces
    if (gamestate->flags.isWhiteTurn) { //means that the piece MUST be a white piece
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

/*
 * returns bitboards containing all checking pieces + all block possibilities + all king moves. If this is 0 => checkmate.
 * also saves the bitboards into config of the gamestate
 *
 * @param gamestate: the gamestate to check
 * @param kingPosition: the position of the king that needs to be checked
 *
 * @return: bitboard with all possible king moves, piece captures or block opportunities
 *
 * */
uint64_t createPossibleMovesAfterCheck(Gamestate* gamestate, Position* kingPosition) {
    uint64_t possibleMovesAfterCheck = 0;
    uint64_t attackingPieces = squareAttacked(gamestate, kingPosition);
    bool side = gamestate->flags.isWhiteTurn;
    uint64_t moves = kingMoves(gamestate, kingPosition);
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

/*
 * makes a move and checks if the move is legal, by seeing if the king is under attack afterwards.
 * this is only filling a new gamestate with the most essential values to check.
 * 
 * @param gamestate: the gamestate to check
 * @param piece: the piece that made the move
 * @param piecePosition: the starting position of the piece
 * @param movePosition: the end position of the piece
 *
 * return: true if the move is legal, otherwise false
 *
 * */
bool makeMoveAndCheckLegal(Gamestate* gamestate, enum PIECE piece, Position* piecePosition, Position* movePosition) {
    Gamestate* newGamestate = gamestateInit();
    bool side = gamestate->flags.isWhiteTurn;
    short originalPos = positionToShort(piecePosition);
    uint64_t notPos = ~positionDictionary.boards[positionToShort(movePosition)];
    
    // bitboards
    newGamestate->bitboards.pawn = gamestate->bitboards.pawn & notPos;
    newGamestate->bitboards.rook = gamestate->bitboards.rook & notPos;
    newGamestate->bitboards.knight = gamestate->bitboards.knight & notPos;
    newGamestate->bitboards.bishop = gamestate->bitboards.bishop & notPos;
    newGamestate->bitboards.queen = gamestate->bitboards.queen & notPos;
    newGamestate->bitboards.king = gamestate->bitboards.king & notPos;
    newGamestate->bitboards.occupancy = gamestate->bitboards.occupancy;
    newGamestate->bitboards.color[side] ^= (positionDictionary.boards[originalPos] | ~notPos);
    newGamestate->bitboards.color[!side] = gamestate->bitboards.color[!side] & notPos;

    switch (piece) {
        case PAWN:
            newGamestate->bitboards.pawn |= ~notPos;
            break;
        case ROOK:
            newGamestate->bitboards.rook |= ~notPos;
            break;
        case KNIGHT:
            newGamestate->bitboards.knight |= ~notPos;
            break;
        case BISHOP:
            newGamestate->bitboards.bishop |= ~notPos;
            break;
        case QUEEN:
            newGamestate->bitboards.queen |= ~notPos;
            break;
        case KING:
            newGamestate->bitboards.king |= ~notPos;
            break;
        default:
           throwError(ERROR_PIECE_NOT_SELECTED, "Error: there was no piece selected to make the Move"); 
    }

    Position* kingPosition = getAllPiecePositions(newGamestate->bitboards.king & gamestate->bitboards.color[side], 1);
    if (squareAttacked(newGamestate, kingPosition)) {
        return false; 
    }
    return true;
}

/**
 * calculates all squares that have pieces that attack the given position
 *
 * @param gamestate: the gamestate to check
 * @param position: the position to check
 *
 * @return: bitboard with a 1 on every position a piece that is attacking the defined position is standing
 * */
uint64_t squareAttacked(Gamestate* gamestate, Position* position) {
    uint64_t moves = 0;
    bool side = gamestate->flags.isWhiteTurn;
    short pos = positionToShort(position);

    moves |= (horizontalSlide(gamestate, pos) | verticalSlide(gamestate, pos)) & (gamestate->bitboards.color[!side] & (gamestate->bitboards.rook | gamestate->bitboards.queen)); 
    moves |= (diagonalSlide(gamestate, pos) | antiDiagonalSlide(gamestate, pos)) & (gamestate->bitboards.color[!side] & (gamestate->bitboards.bishop | gamestate->bitboards.queen));
    moves |= knightMoveDictionary.boards[pos] & (gamestate->bitboards.color[!side] & gamestate->bitboards.knight);
    moves |= (positionDictionary.boards[pos+specialMoveDictionary.pawnCaptureLeft[side]] & fileBitboards.reverseBoards[7])
            & (positionDictionary.boards[pos+specialMoveDictionary.pawnCaptureRight[side]] & fileBitboards.reverseBoards[0])
            & (gamestate->bitboards.color[!side] & gamestate->bitboards.pawn);
    return moves;
}

/**
 * check if a king castle can be done
 *
 * @param gamestate: the gamestate to check
 *
 * @return: true if king castle is possible, else false
 *
 * */
bool canKCastle(Gamestate* gamestate) {
    bool side = gamestate->flags.isWhiteTurn;
    return gamestate->flags.kCastle[side]
        && !(gamestate->bitboards.occupancy & specialMoveDictionary.kingCastle[side])
        && (!squareAttacked(gamestate, &specialMoveDictionary.kingCastlePositions[side][0]) // if squareAttacked is 0, means no piece is attacking this 
        && !squareAttacked(gamestate, &specialMoveDictionary.kingCastlePositions[side][1]));
}

/**
 * check if a queen castle can be done
 *
 * @param gamestate: the gamestate to check
 *
 * @return: true if queen castle is possible, else false
 *
 * */
bool canQCastle(Gamestate* gamestate) {
    bool side = gamestate->flags.isWhiteTurn;
    return gamestate->flags.qCastle[side]
        && !(gamestate->bitboards.occupancy & specialMoveDictionary.queenCastle[side])
        && (!squareAttacked(gamestate, &specialMoveDictionary.queenCastlePositions[side][0]) // if squareAttacked is 0, means no piece is attacking this 
        && !squareAttacked(gamestate, &specialMoveDictionary.queenCastlePositions[side][1]));
}

/**
 * calculates all possible positions a sliding piece (e.g., rook) can have in horizontal (rank) direction
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 *
 * @return: bitboard representing all attacks the piece can make in horizontal direction
 * */
uint64_t horizontalSlide(Gamestate* gamestate, short position) {
    uint64_t mask = rankBitboards.boards[position/8]; 
    uint64_t rankOccupancy = gamestate->bitboards.occupancy & mask;
    uint64_t posInBoard = positionDictionary.boards[position];
    uint64_t moves = ((rankOccupancy - (2 * posInBoard)) ^ (reverseBitboard(reverseBitboard(rankOccupancy) - (2 * positionDictionary.boards[63 - position])))) & mask; 
    moves &= ~(gamestate->bitboards.color[gamestate->flags.isWhiteTurn] & mask);
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
    uint64_t mask = fileBitboards.boards[7 - position%8];
    uint64_t fileOccupancy = gamestate->bitboards.occupancy & mask;
    uint64_t posInBoard = positionDictionary.boards[position];
    uint64_t moves = ((fileOccupancy - (2 * posInBoard)) ^ reverseBitboard(reverseBitboard(fileOccupancy) - (2 * positionDictionary.boards[63 - position]))) & mask;
    moves &= ~(gamestate->bitboards.color[gamestate->flags.isWhiteTurn] & mask);
    return moves;
}

/*
 * calculates all possible positions a sliding piece (e.g., rook) can have in diagonal direction
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 *
 * @return:OPTIMIZATION: instead of calculating the possible moves based on the diagonal/anti diagonal, I could have a lookup table.
 * bitboard representing all attacks the piece can make in diagonal direction
 * */ 
uint64_t antiDiagonalSlide(Gamestate* gamestate, short position) {
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
    moves &= ~(gamestate->bitboards.color[gamestate->flags.isWhiteTurn] & antiDiagonal);
    return moves;
}

/*
 * calculates all possible positions a sliding piece (e.g., rook) can have in diagonal direction
 *
 * @param gamestate: gamestate struct representing the current gamestate
 * @param position: the position of the attacking piece (0 = H1, 7=A1, 63=A8)
 *
 * @return: bitboard representing all attacks the piece can make in diagonal direction
 * */
uint64_t diagonalSlide(Gamestate* gamestate, short position) {
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
    moves &= ~(gamestate->bitboards.color[gamestate->flags.isWhiteTurn] & diagonal);
    return moves;
}
