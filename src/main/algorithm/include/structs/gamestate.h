#ifndef GAMESTATE_H
#define GAMESTATE_H

#include <stdbool.h>

#include "bitboards.h"

enum PIECE {
    PAWN,
    ROOK,
    KNIGHT,
    BISHOP,
    QUEEN,
    KING
};

typedef struct { // initialized to 0
    short rank; // row
    short file; //column
} Position;

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


typedef struct {
    int fullMove;
    int halfMove;
} Counters;

typedef struct { // all initialized to false
    bool whiteKCastle;
    bool whiteQCastle;
    bool blackKCastle;
    bool blackQCastle;
    bool isWhiteTurn;
    bool canEnPassant;
} GamestateFlags;

typedef struct {
    Bitboards bitboards;
    Counters counters;
    GamestateFlags flags;
    Position enPassantPosition;
} Gamestate;

Gamestate gamestateInit();
Position positionInit();

#endif
