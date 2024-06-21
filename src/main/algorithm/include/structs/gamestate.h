#ifndef GAMESTATE_H
#define GAMESTATE_H

#include <stdbool.h>

#include "bitboards.h"
#include "position.h"
#include "move.h"

struct Gamestate;

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
} GamestateConfig;

typedef struct {
    Bitboards bitboards;
    Counters counters;
    GamestateFlags flags;
    Position enPassantPosition;
    GamestateConfig config;
    Move move;
} Gamestate;

Gamestate gamestateInit();
Counters countersInit();
GamestateFlags gamestateFlagInit(); 
GamestateConfig gamestateConfigInit();

#endif
