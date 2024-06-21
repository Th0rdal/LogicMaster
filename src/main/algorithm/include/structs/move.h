#ifndef MOVE_H
#define MOVE_H

#include <stdbool.h>

#include "structs/position.h"

enum PIECE {
    NO_PIECE,
    PAWN,
    ROOK,
    KNIGHT,
    BISHOP,
    QUEEN,
    KING
};

typedef struct {
    bool capture;
    bool promotion;
    bool check;
    bool enPassant;

    bool checkmate;
    bool draw;

    bool whiteKCastle;
    bool whiteQCastle;
    bool blackKCastle;
    bool blackQCastle;

    bool isWhiteTurn;
} MoveFlags;

typedef struct {
    enum PIECE piece;
    Position startPosition;
    Position endPosition;
    MoveFlags flags;
} Move;

Move moveInit();
#endif
