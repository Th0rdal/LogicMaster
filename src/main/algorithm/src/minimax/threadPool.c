#include "minimax/threadPool.h"

MoveGenerationThreadPool moveGenerationThreadPoolInit(short maxDepth, int maxThreads) {
    //init thread pool
    MoveGenerationThreadPool pool;
    pool.maxThreads = maxThreads;
    pool.threads = (pthread_t*) malloc(maxThreads * sizeof(pthread_t));
    if (pool.threads == NULL) {
       throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for move generation worker threads"); 
    }
    pool.queue = queueInit();
    pool.results = queueInit();
    pthread_mutex_init(&pool.lock, NULL);
  
    pool.shutdown = false;
    pool.emergencyShutdown = false;
    pool.workCounter = 0;
    pool.maxDepth = maxDepth;

    return pool;
}

void updateWorkCounter(MoveGenerationThreadPool *pool, int value) {
    pthread_mutex_lock(&pool->lock);
    pool->workCounter += value;
    pthread_mutex_unlock(&pool->lock);
}

void destroyMoveGenerationThreadPoolInit(MoveGenerationThreadPool *pool) {
    pthread_mutex_destroy(&pool->lock);
    destroyQueue(&pool->queue);
    destroyQueue(&pool->results);
}
