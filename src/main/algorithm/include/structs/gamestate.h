#ifndef GAMESTATE_H
#define GAMESTATE_H

#include <stdbool.h>

typedef struct Gamestate Gamestate;
typedef struct GamestateTreeNode GamestateTreeNode; // forward declaration of tree struct
                                                    
#include "bitboards.h"
#include "position.h"
#include "move.h"
#include "structs/tree.h"
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
    GamestateTreeNode* parent;
    GamestateTreeNode* node;
    int evaluation;
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

/*!
 * destroys the given gamestate
 *
 * @param gamestate: pointer to the gamestate to destroy
 *
 * */
void destroyGamestate(Gamestate* gamestate);

/**
 * makes a move on the boards and creates a new gamestate representing the made move.
 *
 * @param gamestate: the current gamestate of the board
 * @param newGamestate: struct to save the new gamestate in
 * @param piece: the piece that made the move
 * @param piecePosition: the current position of the piece that is moving
 * @param movePosition: the end position of the piece that is moving
 * @param promotionPiece: the piece the pawn is promoted to (NO_PIECE if no promotion)
 *
 * @return: false if the move is illegal, else true
 *
 * */
bool gamestateMakeMoveInternal(Gamestate* gamestate, Gamestate* newGamestate, const enum PIECE piece, Position* piecePosition, Position* movePosition, const enum PIECE promotionPiece);

/*!
 * A wrapper function for gamestateMakeMoveInternal, because that demands a PIECE definition for promotionPiece, which is usually NO_PIECE
 * 
 * @param gamestate: the current gamestate of the board
 * @param newGamestate: struct to save the new gamestate in
 * @param piece: the piece that made the move
 * @param piecePosition: the current position of the piece that is moving
 * @param movePosition: the end position of the piece that is moving
 * 
 * @return: false if the move is illegal, else true
 * */
__attribute__((always_inline)) inline bool gamestate_makeMove(Gamestate* gamestate, Gamestate* newGamestate, const enum PIECE piece, Position* piecePosition, Position* movePosition) {
    return gamestateMakeMoveInternal(gamestate, newGamestate, piece, piecePosition, movePosition, NO_PIECE);
}

// here because gamestate needs to be defined first
#include "utility/moveCalculation.h"

#endif
