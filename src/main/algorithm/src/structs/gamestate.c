#include "structs/gamestate.h"

/**
 * initializes all variables with values and returns an initialized gamestate.
 * This should be the only way to get a new gamestate.
 *
 * @return: Gamestate struct after initialization
 */
Gamestate gamestateInit() {
    Gamestate gamestate = {
        .bitboards = bitboardsInit(),
        .flags = gamestateFlagInit(),
        .counters = countersInit(),
        .enPassantPosition = positionInit(),
        .move = moveInit(),
        .config = gamestateConfigInit(),
    };
    return gamestate;
}

/*
 * creates and initializes a Counters struct. Initialization with 0
 *
 * @return: initialized Counters struct
 *
 */
Counters countersInit() {
    Counters counters = {
        .fullMove = 0,
        .halfMove = 0
    };
    return counters;
}

/*
 * creates and initialzes a GamestateFlags struct. Initialization with false
 *
 * @return: initialized GamestateFlags struct
 *
 */
GamestateFlags gamestateFlagInit() {
    GamestateFlags flags = {
        .kCastle = {false, false},
        .qCastle = {false, false},
        .isWhiteTurn = false,
        .canEnPassant = false,
    };
    return flags;
}

/**
 * creates and initializes a GamestateConfig struct. Initializes it with false/Null
 *
 * @return: initialized GamestateConfig
 *
 * */
GamestateConfig gamestateConfigInit() {
    GamestateConfig config;
    config.checkedCheck = false;
    config.lastGamestate = NULL;
    return config;
}

