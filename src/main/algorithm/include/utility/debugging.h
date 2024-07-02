#ifndef DEBUGGING_H
#define DEBUGGING_H

#include <stdio.h>
#include <stdint.h>

#include "structs/gamestate.h"

/*! 
 * prints a bitboard out in a chess board pattern
 *
 * @param board: uint64_t representing the bitboard
 *
 */
void printBoard(uint64_t board);

/*!
 * prints out all variables of gamestate
 *
 * @param gamestate: gamestate struct to print out
 */
void debugGamestate(Gamestate* gamestate);

#endif
