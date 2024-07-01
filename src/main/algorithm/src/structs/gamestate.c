#include "structs/gamestate.h"

Gamestate* gamestateInit() {
    Gamestate* gamestate = (Gamestate*)malloc(sizeof(Gamestate));
    if (gamestate == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for a gamestate");
    }
    
    gamestate->bitboards = bitboardsInit();
    gamestate->flags = gamestateFlagInit();
    gamestate->counters = countersInit();
    gamestate->enPassantPosition = positionInit();
    gamestate->move = moveInit();
    gamestate->config = gamestateConfigInit();
    return gamestate;
}

Counters countersInit() {
    Counters counters = {
        .fullMove = 0,
        .halfMove = 0
    };
    return counters;
}

GamestateFlags gamestateFlagInit() {
    GamestateFlags flags = {
        .kCastle = {false, false},
        .qCastle = {false, false},
        .isWhiteTurn = false,
        .canEnPassant = false,
    };
    return flags;
}


GamestateConfig gamestateConfigInit() {
    GamestateConfig config;
    config.checkedCheck = false;
    config.lastGamestate = NULL;
    config.depth = 0;
    config.parent = NULL;
    config.node = NULL;
    config.evaluation = 0;
    return config;
}

inline void destroyGamestate(Gamestate* gamestate) {
    free(gamestate);
}

bool gamestateMakeMoveInternal(Gamestate* gamestate, Gamestate* newGamestate, const enum PIECE piece, Position* piecePosition, Position* movePosition, const enum PIECE promotionPiece) {
    // if last gamestate turn was black then the isWhiteTurn will be false
    int side = !gamestate->flags.isWhiteTurn ? 1 : 0; // the color that is currently making a move
    int notSide = gamestate->flags.isWhiteTurn ? 1 : 0;
    short pos = positionToShort(movePosition);
    short originalPos = positionToShort(piecePosition);
    uint64_t originalPosBoard = positionDictionary.boards[originalPos];
    uint64_t notOriginalPosBoard = ~originalPosBoard;
    uint64_t posBoard = positionDictionary.boards[pos];
    uint64_t notPosBoard = ~posBoard;

    // bitboards (deleting old position)
    newGamestate->bitboards.pawn = gamestate->bitboards.pawn & (notOriginalPosBoard & notPosBoard);
    newGamestate->bitboards.rook = gamestate->bitboards.rook & (notOriginalPosBoard & notPosBoard);
    newGamestate->bitboards.knight = gamestate->bitboards.knight & (notOriginalPosBoard & notPosBoard);
    newGamestate->bitboards.bishop = gamestate->bitboards.bishop & (notOriginalPosBoard & notPosBoard);
    newGamestate->bitboards.queen = gamestate->bitboards.queen & (notOriginalPosBoard & notPosBoard);
    newGamestate->bitboards.king = gamestate->bitboards.king & (notOriginalPosBoard & notPosBoard);
    newGamestate->bitboards.color[side] = gamestate->bitboards.color[side] ^ (originalPosBoard | posBoard);
    newGamestate->bitboards.color[notSide] = gamestate->bitboards.color[notSide] & notPosBoard;
    newGamestate->bitboards.occupancy = newGamestate->bitboards.color[0] | newGamestate->bitboards.color[1];
    newGamestate->bitboards.possibleMovesAfterCheck = 0xFFFFFFFFFFFFFFFF;
    
    // flags
    newGamestate->flags.kCastle[0] = gamestate->flags.kCastle[0];
    newGamestate->flags.kCastle[1] = gamestate->flags.kCastle[1];
    newGamestate->flags.qCastle[0] = gamestate->flags.qCastle[0];
    newGamestate->flags.qCastle[1] = gamestate->flags.qCastle[1];
    newGamestate->flags.isWhiteTurn = !gamestate->flags.isWhiteTurn;

    switch (piece) {
        case PAWN:
             if (movePosition->rank == rankBitboards.pawnPromotionRank[side]) { // promotion
                newGamestate->move.flags.promotion = true;
                switch (promotionPiece) {
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
                }
            } else { // normal pawn move
                newGamestate->bitboards.pawn |= posBoard;
            }

            if ((originalPos - pos > 0 ? originalPos - pos : -originalPos + pos ) > 9) {
                if ((newGamestate->bitboards.pawn & newGamestate->bitboards.color[notSide]) &
                        ((positionDictionary.boards[pos-1] & fileBitboards.reverseBoards[0]) | (positionDictionary.boards[pos+1] & fileBitboards.reverseBoards[7]))) {
                    
                    newGamestate->flags.canEnPassant = true;
                    // second parameter calculates the spot the pawn would have been if it just moved 1 square          
                    setPositionFromShort(&newGamestate->enPassantPosition, pos-specialMoveDictionary.pawn1SquareAdd[side]); 
                }
            }

            break;
        case ROOK:
            newGamestate->bitboards.rook |= posBoard;
            if (newGamestate->flags.kCastle[side] && piecePosition->rank == specialMoveDictionary.kCastleRookPositions[side][0] && piecePosition->file == specialMoveDictionary.kCastleRookPositions[side][1]) {
                newGamestate->flags.kCastle[side] = false;
            }
            if (newGamestate->flags.qCastle[side] && piecePosition->rank == specialMoveDictionary.qCastleRookPositions[side][0] && piecePosition->file == specialMoveDictionary.qCastleRookPositions[side][1]) {
                newGamestate->flags.qCastle[side] = false; 
            }
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
            if ((newGamestate->flags.kCastle[side] | newGamestate->flags.qCastle[side]) & (piecePosition->rank == movePosition->rank)) {
                if (originalPos - pos > 1) {
                    newGamestate->move.flags.kCastle[side] = true;
                    newGamestate->bitboards.rook ^= specialMoveDictionary.kCastleRookBitboard[side];
                    newGamestate->bitboards.occupancy ^= specialMoveDictionary.kCastleRookBitboard[side];
                    newGamestate->bitboards.color[side] ^= specialMoveDictionary.kCastleRookBitboard[side];
                } else if (originalPos - pos < 1) {
                    newGamestate->move.flags.qCastle[side] = true;
                    newGamestate->bitboards.rook ^= specialMoveDictionary.qCastleRookBitboard[side];
                    newGamestate->bitboards.occupancy ^= specialMoveDictionary.qCastleRookBitboard[side];
                    newGamestate->bitboards.color[side] ^= specialMoveDictionary.qCastleRookBitboard[side];
                }
                newGamestate->flags.kCastle[side] = false;
                newGamestate->flags.qCastle[side] = false;
            }
            break;
        default:
           throwError(ERROR_PIECE_NOT_SELECTED, "Error: there was no piece selected to make the Move"); 
    }
    
    // check if the move is legal, aka king not in check
    Position* kingPosition = getAllPiecePositions(newGamestate->bitboards.king & newGamestate->bitboards.color[side], 1);
    if (squareAttacked(newGamestate, kingPosition, side)) {
        return 0; 
    }

    // counters done here because only bare minimum is done before checking if the move is viable
    newGamestate->counters.fullMove = gamestate->counters.fullMove + 1;
    newGamestate->counters.halfMove = newGamestate->move.flags.capture || piece == PAWN ? 0 : gamestate->counters.halfMove + 1;

    // config done here because only bare minimum is done before checking if the move is viable
    newGamestate->config.lastGamestate = gamestate;
    newGamestate->config.depth = gamestate->config.depth + 1;
   
    newGamestate->move.piece = newGamestate->move.flags.promotion ? promotionPiece : piece;
    newGamestate->move.startPosition = *piecePosition;
    newGamestate->move.endPosition = *movePosition;
    newGamestate->move.flags.capture = gamestate->bitboards.occupancy & positionDictionary.boards[pos];
    newGamestate->move.flags.isWhiteTurn = !gamestate->flags.isWhiteTurn; // this move is of opposite color from last move
    newGamestate->move.flags.draw = false;

    // new Gamestate fully filled
    kingPosition = getAllPiecePositions(newGamestate->bitboards.king & newGamestate->bitboards.color[notSide], 1);
    if (squareAttacked(newGamestate, kingPosition, notSide)) {
        newGamestate->move.flags.check = true;
        newGamestate->bitboards.possibleMovesAfterCheck = createPossibleMovesAfterCheck(newGamestate, kingPosition, notSide);
        if (newGamestate->bitboards.possibleMovesAfterCheck == 0) {
            newGamestate->move.flags.checkmate = true;
        }
    }
    return 1;
}
