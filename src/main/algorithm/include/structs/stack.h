#ifndef STACK_H
#define STACK_H

#include "structs/tree.h"

#define MAX_STACK_SIZE 10

typedef struct EvaluationData {
    short state; // if state = 0, push children. if state = 1, process evaluation
    GamestateTreeNode* node;
} EvaluationData;

typedef struct EvaluationStack {
   EvaluationData items[MAX_STACK_SIZE];
   int top;
} EvaluationStack;

/*!
 * allocates memory and initializes the stack pointer
 *
 * @return: pointer to the EvaluationStack struct
 *
 * */
EvaluationStack* initializeStack();

/*!
 * checks if the stack is full
 *
 * @param stack: pointer to the EvaluationStack to check
 *
 * @return: true if the stack is full, else false
 *
 * */
bool isFull(const EvaluationStack* stack);

/*!
 * check if the stack is empty
 *
 * @param stack: pointer to the EvaluationStack to check
 * 
 * @return: true if the stack is empty, else false
 *
 * */
bool isEmpty(const EvaluationStack* stack);

/*!
 * pushes a EvaluationData struct onto the stack
 *
 * @param stack: the stack to push the data on
 * @param data: the data to push on the stack
 *
 * @warning ERROR_STACK_OVERFLOW: if the stack is full
 *
 * */
void pushEvaluationStack(EvaluationStack* stack, const EvaluationData* data);

/*!
 * pops the last element pushed onto the stack and returns it. It also deletes it from the stack
 *
 * @param stack: the stack to pop from
 *
 * @return: the last element pushed onto the stack as EvaluationData pointer
 *
 * @warning ERROR_STACK_UNDERFLOW: if the stack is empty
 * */
EvaluationData* popEvaluationStack(EvaluationStack* stack);

/*!
 * pooks at the last element pushed onto the stack and returns it.
 *
 * @param stack: the stack to pop from
 * 
 * @return: the last element pushed onto the stack as EvaluationData pointer
 *
 * @warning ERROR_STACK_UNDERFLOW: if the stack is empty
 * */
const EvaluationData* peekEvaluationStack(const EvaluationStack* stack);

/*!
 * creates an EvaluationData struct with the given data
 *
 * @param node: the GamestateTreeNode to save in EvaluationData
 * @param state: the current state of the node. If state = 0, push children. if state = 1, process evaluation
 *
 * @return: pointer to the initialized EvaluationData struct
 *
 * */
EvaluationData* createEvaluationData(GamestateTreeNode* node, const short state);
#endif
