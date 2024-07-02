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

/*!
 * initializes the Move struct
 *
 * */
Move moveInit();

/*!
 * creates the given chess move in long notation and saves it in the given char*
 *
 * @param move: The move to print
 * @param string: the char* to save the result in 
 *
 * */
void printMove(const Move* move, char* string);

#endif
