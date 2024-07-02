/**
 * @file evaluation.h
 *
 * This file includes function to do with evaluating chess positions as well as the evaluation worker
 *
 * */
#ifndef EVALUATION_H
#define EVALUATION_H

#define PAWNVALUE 1
#define ROOKVALUE 5
#define KNIGHTVALUE 3
#define BISHOPVALUE 3
#define QUEENVALUE 9
#define CHECKMATE 100
#define CHECK 10
#define DRAW 0

#include <limits.h>
#include <stdbool.h>

#include "structs/gamestate.h"
#include "structs/stack.h"
#include "config.h"

// MAX_WAIT_TIME is set in minimax.h

typedef struct {
    HANDLE* threads;
    int maxThreads;
    bool shutdown;
    int workCounter;
    GamestateTreeNode* srcTree;
    CRITICAL_SECTION lock;
}EvaluationThreadPool;

/*!
 * The function an evaluation worker thread executes to evaluate gamestates
 * For evaluating moves it uses the internal function evaluate
 *
 * @param lpParam: The EvaluationWorkerPoolThread with all needed variables set
 *
 * */
DWORD WINAPI evaluationWorker(LPVOID lpParam);

/*!
 * initializes all variables of the struct
 *
 * @param maxthreads: the maximal amount of threads the pool may have
 * @param srcTree: the base node for the evaluation
 *
 * @return: the initialized EvaluationThreadPool pointer 
 * */
EvaluationThreadPool* evaluationThreadPoolInit(const int maxThreads, GamestateTreeNode* srcTree);

/*!
 * evaluates the best evaluation value out of all children and sets it for the gamestate.
 * This should only be used with GamestateTreeNodes that have children that are already evaluated
 *
 * @param gamestateTreeNode: the node to evaluate
 *
 * */
Gamestate* evaluateLastGamestate(GamestateTreeNode* gamestateTreeNode);

/*!
 * destroys the EvaluationThreadPool struct and all needed fields
 *
 * @param pool: the struct to destroy
 *
 * */
void destroyEvaluationThreadPool(EvaluationThreadPool* pool);

/*!
 * starts evaluating the gamestate tree
 * */
void evaluation_start(int maxThreads);
#endif
