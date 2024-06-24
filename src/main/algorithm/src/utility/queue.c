#include "utility/queue.h"


Queue queueInit(queueNode* listHead) {
    Queue queue = {
        .front = 0,
        .rear = 0,
        .head = listHead
    };
    InitializeCriticalSection(&queue.lock);
    InitializeConditionVariable(&queue.condEnqueue);
    InitializeConditionVariable(&queue.condDequeue);
    return queue;
}

void enqueue(Queue *queue, Gamestate* gamestate) {
    EnterCriticalSection(&queue->lock);

    while ((queue->rear+1) % QUEUE_SIZE == queue->front) {
        SleepConditionVariableCS(&queue->condEnqueue, &queue->lock, INFINITE);
    }

    queueNode* newNode = malloc(sizeof(queueNode));
    if (newNode == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for queueNode");
    }
    newNode->gamestate = gamestate;
    newNode->next = queue->head;
    queue->head = newNode;
    queue->data[queue->rear] = gamestate;
    queue->rear = (queue->rear+1) % QUEUE_SIZE;

    WakeConditionVariable(&queue->condDequeue);
    LeaveCriticalSection(&queue->lock);
}

Gamestate* dequeue(Queue *queue) {
    EnterCriticalSection(&queue->lock);

    while (queue->front == queue->rear) {
        SleepConditionVariableCS(&queue->condDequeue, &queue->lock, INFINITE);
    }

    Gamestate* gamestate = queue->data[queue->front];
    queue->front = (queue->front+1) % QUEUE_SIZE;
    
    WakeConditionVariable(&queue->condEnqueue);
    LeaveCriticalSection(&queue->lock);
    return gamestate;
}

void destroyQueue(Queue *queue) {
    DeleteCriticalSection(&queue->lock);
    free(queue);
}
