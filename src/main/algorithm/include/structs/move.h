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

    bool kCastle[2];
    bool qCastle[2];

    bool isWhiteTurn;
} MoveFlags;

typedef struct {
    enum PIECE piece;
    Position startPosition;
    Position endPosition;
    MoveFlags flags;
} Move;

Move moveInit();
void printMove(Move* move, char* string);
#endif
