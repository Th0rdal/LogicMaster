#ifndef THREADPOOL_H
#define THREADPOOL_H

#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#include "utility/queue.h"

typedef struct {
    int threadId;
}ThreadArgs;

typedef struct {
    HANDLE* threads;
    int maxThreads;
    Queue queue;
    bool shutdown;
    int workCounter;
    int maxDepth;
    Queue results;
    CRITICAL_SECTION lock;
} MoveGenerationThreadPool;

/*!
 * initializes all variables of the struct
 *
 * @param maxDepth: the maximal depth of the move generation
 * @param maxthreads: the maximal amount of threads the pool may have
 *
 * @return: the initialized MoveGenerationThreadPool pointer 
 * */
MoveGenerationThreadPool* moveGenerationThreadPoolInit(const short maxDepth, const int maxThreads, queueNode* workQueue, queueNode* resultNode);

/*!
 * updates the workCounter variable of the given Mpool struct
 *
 * @param pool: the pool variable with the workCounter variable
 * @param value: the update value for workCounter. The value gets added to workCounter
 *
 * */
void updateWorkCounter(MoveGenerationThreadPool* pool, const int value);

/*!
 * destroys the MoveGenerationThreadPool struct and all needed fields
 *
 * @param pool: the struct to destroy
 *
 * */
void destroyMoveGenerationThreadPoolInit(MoveGenerationThreadPool *pool);
#endif
