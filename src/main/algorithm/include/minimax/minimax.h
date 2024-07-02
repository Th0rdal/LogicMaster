#ifndef MINIMAX_H
#define MINIMAX_H

#include <windows.h>
#include <stdbool.h>

#include "preprocessing.h"
#include "evaluation.h"

Gamestate* minimax(const short maxDepth, int maxThreads, Gamestate* gamestate);

#endif
