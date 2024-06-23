#ifndef MINIMAX_H
#define MINIMAX_H

#include <windows.h>

#include "preprocessing.h"

void minimax(short maxDepth, Gamestate* gamestate);

extern Gamestate* endStates;

#endif