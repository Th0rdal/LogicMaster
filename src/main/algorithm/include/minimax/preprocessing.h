#ifndef PREPROCESSING_H
#define PREPROCESSING_H

#include <stdlib.h>

#include "errors.h"
#include "structs/gamestate.h"
#include "structs/position.h"
#include "utility/queue.h"
#include "utility/moveCalculation.h"
#include "minimax/threadPool.h"

void minimax_preprocessing(short maxDepth, int maxThreads, Gamestate* gamestate);
int calculateMoves(Gamestate* gamestate, Queue* queue); 

#endif
