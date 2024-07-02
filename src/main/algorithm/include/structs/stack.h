#ifndef STACK_H
#define STACK_H

#include "structs/tree.h"
#include "config.h"

typedef struct EvaluationData {
    short state; // if state = 0, push children. if state = 1, process evaluation
    GamestateTreeNode* node;
} EvaluationData;

typedef struct EvaluationStack {
   EvaluationData** items;
   int top;
   int MAX_STACK_SIZE;
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
__attribute__((always_inline)) inline bool isFull(const EvaluationStack* stack) {
    return stack->top == stack->MAX_STACK_SIZE - 1;
}

/*!
 * check if the stack is empty
 *
 * @param stack: pointer to the EvaluationStack to check
 * 
 * @return: true if the stack is empty, else false
 *
 * */
__attribute__((always_inline)) inline bool isEmpty(const EvaluationStack* stack) {
    return stack->top == -1;
}

/*!
 * pushes a EvaluationData struct onto the stack
 *
 * @param stack: the stack to push the data on
 * @param data: the data to push on the stack
 *
 * @warning ERROR_STACK_OVERFLOW: if the stack is full
 *
 * */
__attribute__((always_inline)) inline void pushEvaluationStack(EvaluationStack* stack, EvaluationData* data) {
    if (isFull(stack)) {
        throwWarning(WARNING_STACK_TOO_SMALL, "Warning: The stack was too small");
        #ifdef STACK_RESIZE
            throwWarning(WARNING_RESIZING_STACK, "Warning: The stack was resized");
            stack->MAX_STACK_SIZE *= 2; 
            EvaluationData** temp = (EvaluationData**)realloc(stack->items, sizeof(EvaluationData*) * stack->MAX_STACK_SIZE); 
            if (temp == NULL) {
                throwError(ERROR_MEMORY_REALLOC_FAILED, "Error: failed to resize the stack items");
            }
            stack->items = temp;
        #endif
        #ifndef STACK_RESIZE
            throwError(ERROR_CUSTOM_STACK_OVERFLOW, "Error: trying to push on a full EvaluationStack");
        #endif
    }
    stack->items[++(stack->top)] = data;
} 

/*!
 * pops the last element pushed onto the stack and returns it. It also deletes it from the stack
 *
 * @param stack: the stack to pop from
 *
 * @return: the last element pushed onto the stack as EvaluationData pointer
 *
 * @warning ERROR_STACK_UNDERFLOW: if the stack is empty
 * */
__attribute__((always_inline)) inline EvaluationData* popEvaluationStack(EvaluationStack* stack) {
    if (isEmpty(stack)) {
        throwError(ERROR_CUSTOM_STACK_UNDERFLOW, "Error: trying to pop from empty EvaluationStack");
    }
    return stack->items[(stack->top)--];
}

/*!
 * pooks at the last element pushed onto the stack and returns it.
 *
 * @param stack: the stack to pop from
 * 
 * @return: the last element pushed onto the stack as EvaluationData pointer
 *
 * @warning ERROR_STACK_UNDERFLOW: if the stack is empty
 * */
__attribute__((always_inline)) inline const EvaluationData* peekEvaluationStack(const EvaluationStack* stack) {
    if (isEmpty(stack)) {
        throwError(ERROR_CUSTOM_STACK_UNDERFLOW, "Error: trying to peek from empty EvaluationStack");
    }
    return (stack->items[stack->top]);
}

/*!
 * creates an EvaluationData struct with the given data
 *
 * @param node: the GamestateTreeNode to save in EvaluationData
 * @param state: the current state of the node. If state = 0, push children. if state = 1, process evaluation
 *
 * @return: pointer to the initialized EvaluationData struct
 *
 * */
__attribute__((always_inline)) inline EvaluationData* createEvaluationData(GamestateTreeNode* node, const short state) {
    EvaluationData* data = (EvaluationData*)malloc(sizeof(EvaluationData));
    data->node = node;
    data->state = state;
    return data;
}

#endif
