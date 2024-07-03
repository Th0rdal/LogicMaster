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
#define CHECKMATE 1000
#define CHECK 20
#define DRAW 0
#define CASTLE 10

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
 * @param args: command args struct pointer with configuration
 * @param srcTree: the base node for the evaluation
 *
 * @return: the initialized EvaluationThreadPool pointer 
 * */
EvaluationThreadPool* evaluationThreadPoolInit(command_args* args, GamestateTreeNode* srcTree);

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
 *
 * @param args: command args struct pointer with configuration
 * */
void evaluation_start(command_args* args);
#endif
