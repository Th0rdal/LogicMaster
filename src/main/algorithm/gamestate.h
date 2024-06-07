#ifndef GAMESTATE_H
#define GAMESTATE_H

#include <stdbool.h>

enum PIECE {
    PAWN,
    ROOK,
    KNIGHT,
    BISHOP,
    QUEEN,
    KING
}

typedef struct {
    short int rank;
    short int file;
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
    PIECE piece;
    Position startPosition;
    Position endPosition;
    MoveFlags flags;
} Move;


typedef struct {
    int fullMove;
    int halfMove;
} Counters;

typedef struct {
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

void gamestateCorrectlyLoadedCheck(Gamestate* gamestate);
Gamestate* gameStateInit();

extern Gamestate gamestate;

#endif
