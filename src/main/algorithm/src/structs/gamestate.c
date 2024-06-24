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
    return config;
}

bool gamestate_makeMove(Gamestate* gamestate, Gamestate* newGamestate, const enum PIECE piece, Position* piecePosition, Position* movePosition) {


    newGamestate->move.piece = piece;
    deepCopyPosition(&newGamestate->move.startPosition, piecePosition);
    deepCopyPosition(&newGamestate->move.endPosition, movePosition);

    bool side = gamestate->flags.isWhiteTurn;
    short pos = positionToShort(movePosition);
    short originalPos = positionToShort(piecePosition);
    uint64_t notPos = ~positionDictionary.boards[pos];
    newGamestate->move.flags.capture = gamestate->bitboards.occupancy & positionDictionary.boards[pos];
    newGamestate->move.flags.isWhiteTurn = side;
    newGamestate->move.flags.draw = false;


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
    newGamestate->bitboards.possibleMovesAfterCheck = 0xFFFFFFFFFFFFFFFF;
    
    // flags
    newGamestate->flags.kCastle[0] = gamestate->flags.kCastle[0];
    newGamestate->flags.kCastle[1] = gamestate->flags.kCastle[1];
    newGamestate->flags.qCastle[0] = gamestate->flags.qCastle[0];
    newGamestate->flags.qCastle[1] = gamestate->flags.qCastle[1];
    newGamestate->flags.isWhiteTurn = side;

    switch (piece) {
        case PAWN:
            newGamestate->bitboards.pawn |= ~notPos;
            if ((originalPos - pos > 0 ? originalPos - pos : -originalPos + pos ) > 9) {
                if ((newGamestate->bitboards.pawn & newGamestate->bitboards.color[!side]) &
                        ((positionDictionary.boards[pos-1] & fileBitboards.reverseBoards[0]) | (positionDictionary.boards[pos+1] & fileBitboards.reverseBoards[7]))) {
                    
                    newGamestate->flags.canEnPassant = true;
                    // second parameter calculates the spot the pawn would have been if it just moved 1 square          
                    setPositionFromShort(&newGamestate->enPassantPosition, pos-specialMoveDictionary.pawn1SquareAdd[side]); 
                }
            }
            if (movePosition->rank == rankBitboards.pawnPromotionRank[side]) {
                newGamestate->move.flags.promotion = true;
            } 
            break;
        case ROOK:
            newGamestate->bitboards.rook |= ~notPos;
            if (newGamestate->flags.kCastle[side] && piecePosition->rank == specialMoveDictionary.kCastleRookPositions[side][0] && piecePosition->file == specialMoveDictionary.kCastleRookPositions[side][1]) {
                newGamestate->flags.kCastle[side] = false;
            }
            if (newGamestate->flags.qCastle[side] && piecePosition->rank == specialMoveDictionary.qCastleRookPositions[side][0] && piecePosition->file == specialMoveDictionary.qCastleRookPositions[side][1]) {
                newGamestate->flags.qCastle[side] = false; 
            }
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
    Position* kingPosition = getAllPiecePositions(newGamestate->bitboards.king & gamestate->bitboards.color[side], 1);
    if (squareAttacked(newGamestate, kingPosition)) {
        return 0; 
    }

    // counters done here because only bare minimum is done before checking if the move is viable
    newGamestate->counters.fullMove = gamestate->counters.fullMove++;
    newGamestate->counters.halfMove = newGamestate->move.flags.capture || piece == PAWN ? 0 : gamestate->counters.halfMove++;

    // config done here because only bare minimum is done before checking if the move is viable
    newGamestate->config.lastGamestate = gamestate;
    newGamestate->config.depth = gamestate->config.depth++;
   
    side = !side;
    newGamestate->flags.isWhiteTurn = side;
    // new Gamestate fully filled
    kingPosition = getAllPiecePositions(newGamestate->bitboards.king & gamestate->bitboards.color[side], 1);
    if (squareAttacked(newGamestate, kingPosition)) {
        newGamestate->move.flags.check = true;
        newGamestate->bitboards.possibleMovesAfterCheck = createPossibleMovesAfterCheck(newGamestate, kingPosition);
        if (newGamestate->bitboards.possibleMovesAfterCheck == 0) {
            newGamestate->move.flags.checkmate = true;
        }
    }
    return 1;
}
