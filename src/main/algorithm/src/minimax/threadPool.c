#include "minimax/threadPool.h"


MoveGenerationThreadPool* moveGenerationThreadPoolInit(const short maxDepth, const int maxThreads, queueNode* workNode, queueNode* resultNode) {
    MoveGenerationThreadPool* pool = (MoveGenerationThreadPool*)malloc(sizeof(MoveGenerationThreadPool));
    if (pool == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for moveGenerationThreadPool");
    }
    pool->maxThreads = maxThreads;
    pool->threads = (HANDLE*) malloc(maxThreads * sizeof(HANDLE));
    if (pool->threads == NULL) {
       throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for move generation worker threads"); 
    }
    pool->queue = queueInit(workNode);
    pool->results = queueInit(resultNode);
    InitializeCriticalSection(&pool->lock);
  
    pool->shutdown = false;
    pool->workCounter = 0;
    pool->maxDepth = maxDepth;

    return pool;
}

void updateWorkCounter(MoveGenerationThreadPool *pool, const int value) {
    EnterCriticalSection(&pool->lock);
    pool->workCounter += value;
    LeaveCriticalSection(&pool->lock);
}

void destroyMoveGenerationThreadPoolInit(MoveGenerationThreadPool *pool) {
    DeleteCriticalSection(&pool->lock);
    free(pool->threads);
    destroyQueue(&pool->queue);
    destroyQueue(&pool->results);
    free(pool);
}
