#ifndef QUEUE_H
#define QUEUE_H

//this is a THREAT SAVE queue
#define QUEUE_SIZE 500

#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

#include "structs/gamestate.h"

typedef struct {
    Gamestate* data[QUEUE_SIZE];
    int front;
    int rear;
    CRITICAL_SECTION lock;
    CONDITION_VARIABLE condEnqueue; // waiting in enqueue when queue full
    CONDITION_VARIABLE condDequeue; // waiting in dequeue when queue is empty
} Queue;

Queue queueInit();
void enqueue(Queue* queue, Gamestate* gamestate);
Gamestate* dequeue(Queue* queue);
void destroyQueue(Queue* queue);
#endif
