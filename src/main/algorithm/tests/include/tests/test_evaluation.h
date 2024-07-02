#ifndef TEST_EVALUATION_H
#define TEST_EVALUATION_H

#include "unity.h"
#include "minimax/evaluation.h"
#include "minimax/preprocessing.h"

#include "mockExit.h"

extern int evaluate(Gamestate* gamestate);

void includeTests_evaluation();
void runTests_evaluation();
#endif
