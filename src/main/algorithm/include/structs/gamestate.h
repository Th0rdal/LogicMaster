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

/*!
 * initializes all variables with values and returns an initialized gamestate.
 * This should be the only way to get a new gamestate.
 *
 * @return: Gamestate struct after initialization
 */
Gamestate* gamestateInit();

/*!
 * creates and initializes a Counters struct. Initialization with 0
 *
 * @return: initialized Counters struct
 *
 */
Counters countersInit();

/*!
 * creates and initialzes a GamestateFlags struct. Initialization with false
 *
 * @return: initialized GamestateFlags struct
 *
 */
GamestateFlags gamestateFlagInit(); 

/*!
 * creates and initializes a GamestateConfig struct. Initializes it with false/Null
 *
 * @return: initialized GamestateConfig
 *
 * */
GamestateConfig gamestateConfigInit();

/**
 * makes a move on the boards and creates a new gamestate representing the made move.
 *
 * @param gamestate: the current gamestate of the board
 * @param newGamestate: struct to save the new gamestate in
 * @param piece: the piece that made the move
 * @param piecePosition: the current position of the piece that is moving
 * @param movePosition: the end position of the piece that is moving
 *
 * @return: false if the move is illegal, else true
 *
 * */
bool gamestate_makeMove(Gamestate* gamestate, Gamestate* newGamestate, const enum PIECE piece, Position* piecePosition, Position* movePosition);

// here because gamestate needs to be defined first
#include "utility/moveCalculation.h"

#endif
