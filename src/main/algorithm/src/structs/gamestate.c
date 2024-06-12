#include "structs/gamestate.h"


/**
 * initializes all variables with values and returns an initialized gamestate.
 * This should be the only way to get a new gamestate.
 *
 * @return: Gamestate struct after initialization
 */
Gamestate gamestateInit() {
    Gamestate gamestate;
    gamestate.bitboards = bitboardsInit();
    gamestate.flags = gamestateFlagInit();
    gamestate.counters = countersInit();
    gamestate.enPassantPosition = positionInit();
    return gamestate;
}

/*
 * creates and initializes a Counters struct. Initialization with 0
 *
 * @return: initialized Counters struct
 *
 */
Counters countersInit() {
    Counters counters;
    counters.fullMove = 0;
    counters.halfMove = 0;
    return counters;
}

/*
 * creates and initialzes a GamestateFlags struct. Initialization with false
 *
 * @return: initialized GamestateFlags struct
 *
 */
GamestateFlags gamestateFlagInit() {
    GamestateFlags flags;
    flags.whiteKCastle = false;
    flags.whiteQCastle = false;
    flags.blackKCastle = false;
    flags.blackQCastle = false;
    flags.isWhiteTurn = false;
    flags.canEnPassant = false;
    return flags;
}

/*
 * creates and initializes a Position struct. Initialization with 0
 *
 * @return: initialized Position struct
 *
 */
Position positionInit() {
    Position position;
    position.rank = 0;
    position.file = 0;
    return position;
}

