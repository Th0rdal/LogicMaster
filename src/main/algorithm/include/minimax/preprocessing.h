#ifndef PREPROCESSING_H
#define PREPROCESSING_H

#define AVERAGE_MOVES 35
#define MOVES_INCREMENT 5

#include <stdlib.h>

#include "errors.h"
#include "structs/gamestate.h"
#include "structs/position.h"

void minimax_preprocessing(short maxDepth, Gamestate* gamestate);

#endif
