#ifndef MINIMAX_H
#define MINIMAX_H

#include <windows.h>
#include <stdbool.h>

#include "preprocessing.h"
#include "evaluation.h"
#include "config.h"

/*!
 * Implements the algorithm pipeline. preprocessing (move generation) -> evaluation -> post preprocessing -> return
 *
 * @param args: command args struct pointer with configuration
 * @param gamestate: an Gamestate struct pointer to write the result of the algorithm into
 *
 * */
Gamestate* minimax(command_args* args, Gamestate* gamestate);

/*!
 * Finds the given gamestate in the tree and prints all possible moves that can be made from that gamestate.
 * That is done by printing all children of the TreeNode of the gamestate
 *
 * @param args: command args struct pointer with configuration
 * @param gamestate: an Gamestate struct pointer to write the result of the algorithm into
 *
 * */
void printAllPossibleMoves(command_args* args, Gamestate* gamestate);

#endif
