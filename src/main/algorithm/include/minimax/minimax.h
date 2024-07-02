#ifndef MINIMAX_H
#define MINIMAX_H

#include <windows.h>
#include <stdbool.h>

#include "preprocessing.h"
#include "evaluation.h"

extern bool algorithmColorWhite; // if true, the algorithm will optimize for white

Gamestate* minimax(const short maxDepth, bool algorithmColorWhite, Gamestate* gamestate);

#endif
