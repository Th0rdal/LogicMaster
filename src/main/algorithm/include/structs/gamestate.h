#ifndef GAMESTATE_H
#define GAMESTATE_H

#include <stdbool.h>

struct Gamestate;

#include "bitboards.h"
#include "position.h"
#include "move.h"
//#include "utility/moveCalculation.h" at the bottom as gamestate needs to be defined first

typedef struct {
    int fullMove;
    int halfMove;
} Counters;

typedef struct { // all initialized to false
    bool kCastle[2]; // index 0 = black, index 1 = white
    bool qCastle[2];
    bool isWhiteTurn;
    bool canEnPassant;
} GamestateFlags;


typedef struct {
    bool checkedCheck;
    struct Gamestate* lastGamestate; //forward declaration
    short depth;
} GamestateConfig;

typedef struct Gamestate {
    Bitboards bitboards;
    Counters counters;
    GamestateFlags flags;
    Position enPassantPosition;
    GamestateConfig config;
    Move move;
} Gamestate;

Gamestate* gamestateInit();
Counters countersInit();
GamestateFlags gamestateFlagInit(); 
GamestateConfig gamestateConfigInit();
bool gamestate_makeMove(Gamestate* gamestate, Gamestate* newGamestate, enum PIECE piece, Position* piecePosition, Position* movePosition);

// here because gamestate needs to be defined first
#include "utility/moveCalculation.h"

#endif
