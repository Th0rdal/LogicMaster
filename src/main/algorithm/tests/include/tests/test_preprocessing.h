#ifndef TEST_PREPROCESSING_H
#define TEST_PREPROCESSING_H

#include <windows.h>
#include <math.h>
#include <time.h>

#include "minimax/preprocessing.h"

#include "structs/move.h"
#include "unity.h"

extern void includeTests_preprocessing();
extern void runTests_preprocessing();

// needed for testing but not exposed in their header
extern int calculateMoves(Gamestate* gamestate, Queue* queue, GamestateTreeNode* node); 
DWORD WINAPI generationWorker(LPVOID lpParamo);

#endif
