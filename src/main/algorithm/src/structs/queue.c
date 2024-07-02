#include "structs/queue.h"


Queue* queueInit(int maxDepth) {
    Queue* queue = (Queue*)malloc(sizeof(Queue));
    if (queue == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for a queue");
    }
    queue->front = 0;
    queue->rear = 0;
    queue->maxSize = QUEUE_SIZE_MULTIPLIER * pow(QUEUE_SIZE_BASE, maxDepth);
    queue->data = (Gamestate**)malloc(sizeof(Gamestate*) * queue->maxSize);
    InitializeCriticalSection(&queue->lock);    
    InitializeConditionVariable(&queue->condEnqueue);
    InitializeConditionVariable(&queue->condDequeue);
    return queue;
}

void enqueue(Queue *queue, Gamestate* gamestate) {
    EnterCriticalSection(&queue->lock);
    
    while ((queue->rear+1) % queue->maxSize == queue->front) {
        if (!SleepConditionVariableCS(&queue->condEnqueue, &queue->lock, ENQUEUE_WAIT_TIME)) {
            throwWarning(WARNING_QUEUE_TOO_SMALL, "Warning: The queue was too small and a thread waited over %d seconds", ENQUEUE_WAIT_TIME);
            #ifdef QUEUE_RESIZE
                throwWarning(WARNING_RESIZING_QUEUE, "Warning: The queue was resized");
                queue->maxSize *= 10;
                Gamestate** temp = (Gamestate**)realloc(queue->data, sizeof(Gamestate*) * queue->maxSize);
                if (temp == NULL) {
                    throwError(ERROR_MEMORY_REALLOC_FAILED, "Error: failed to resize the queue data");
                }
                queue->data = temp;
            #endif /* ifdef QUEUE_RESIZE */
            #ifndef QUEUE_RESIZE
                throwError(ERROR_QUEUE_TOO_SMALL, "Error: The queue is too small and resizing is not activated. To active resizing define 'QUEUE_RESIZE'");
            #endif
            LeaveCriticalSection(&queue->lock);
        }
    }

    queue->data[queue->rear] = gamestate;
    queue->rear = (queue->rear+1) % queue->maxSize;

    WakeConditionVariable(&queue->condDequeue);
    LeaveCriticalSection(&queue->lock);
}

Gamestate* dequeue(Queue *queue) {
    EnterCriticalSection(&queue->lock);
    while (queue->front == queue->rear) {
        if (!SleepConditionVariableCS(&queue->condDequeue, &queue->lock, DEQUEUE_WAIT_TIME)) {
            LeaveCriticalSection(&queue->lock);
            return NULL;
        }
    }

    Gamestate* gamestate = queue->data[queue->front];
    queue->front = (queue->front+1) % queue->maxSize;
   
    WakeConditionVariable(&queue->condEnqueue);
    LeaveCriticalSection(&queue->lock);
    return gamestate;
}

void destroyQueue(Queue *queue) {
    DeleteCriticalSection(&queue->lock);
    free(queue);
}
