#include "structs/move.h"

MoveFlags moveFlagsInit();

Move moveInit() {
    Move move;
    move.piece = NO_PIECE;
    move.startPosition = positionInit();
    move.endPosition = positionInit();
    move.flags = moveFlagsInit(); 
    return move;
}

MoveFlags moveFlagsInit() {
    MoveFlags flags;
    flags.capture = false;
    flags.promotion = false;
    flags.check = false;
    flags.enPassant = false;
    flags.checkmate = false;
    flags.draw = false;
    flags.whiteKCastle = false;
    flags.whiteQCastle = false;
    flags.blackKCastle = false;
    flags.blackQCastle = false;
    flags.isWhiteTurn = false;
    return flags;
}
