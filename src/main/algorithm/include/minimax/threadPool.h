#ifndef THREADPOOL_H
#define THREADPOOL_H

#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#include "utility/queue.h"

typedef struct {
    pthread_t* threads;
    int maxThreads;
    Queue queue;
    bool shutdown;
    bool emergencyShutdown;
    int workCounter;
    int maxDepth;
    Queue results;
    pthread_mutex_t lock;
} MoveGenerationThreadPool;

MoveGenerationThreadPool moveGenerationThreadPoolInit(short maxDepth, int maxThreads);
void updateWorkCounter(MoveGenerationThreadPool* pool, int value);
void destroyMoveGenerationThreadPoolInit(MoveGenerationThreadPool *pool);
#endif
