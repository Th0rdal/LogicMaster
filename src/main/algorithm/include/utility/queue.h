#ifndef QUEUE_H
#define QUEUE_H

//this is a THREAT SAVE queue
#define QUEUE_SIZE 500

#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

#include "structs/gamestate.h"

typedef struct queueNode{
    Gamestate* gamestate;
    struct queueNode* next;
}queueNode;

typedef struct {
    Gamestate* data[QUEUE_SIZE];
    int front;
    int rear;
    queueNode* head;
    CRITICAL_SECTION lock;
    CONDITION_VARIABLE condEnqueue; // waiting in enqueue when queue full
    CONDITION_VARIABLE condDequeue; // waiting in dequeue when queue is empty
} Queue;

/*!
 * initializes the queue struct
 *
 * */
Queue queueInit();

/*!
 * adds a Gamestate to the queue
 *
 * @param queue: the queue to add the gamestate to
 * @param gamestate: the gamestate to add
 *
 * */
void enqueue(Queue* queue, Gamestate* gamestate);

/*!
 * gets the first gamestate of the queue
 *
 * @param queue: the queue to get the gamestate from
 *
 * */
Gamestate* dequeue(Queue* queue);

/*!
 * destroies the queue struct and all fields in it
 *
 * */
void destroyQueue(Queue* queue);
#endif
