#ifndef PREPROCESSING_H
#define PREPROCESSING_H

#include <stdlib.h>

#include "errors.h"
#include "structs/gamestate.h"
#include "structs/position.h"
#include "utility/queue.h"
#include "utility/moveCalculation.h"
#include "minimax/threadPool.h"

void minimax_preprocessing(const short maxDepth, const int maxThreads, Gamestate* gamestate);

/*!
 * calculates all possible moves for a given gamestate, create new gamestate and save it in the queue
 *
 * @param gamestate: the gamestate to use for the calculations
 * @param queue: the queue to push the results in
 *
 * @return: number of gamestates added to the queue
 *
 * */
int calculateMoves(Gamestate* gamestate, Queue* queue); 

#endif
