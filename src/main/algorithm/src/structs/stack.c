#include "structs/stack.h"

inline EvaluationStack* initializeStack() {
    EvaluationStack* stack = (EvaluationStack*)malloc(sizeof(EvaluationStack));
    stack->top = -1;
    return stack;
}

inline bool isFull(const EvaluationStack* stack) {
    return stack->top == MAX_STACK_SIZE - 1;
}

inline bool isEmpty(const EvaluationStack* stack) {
    return stack->top == -1;
}

inline void pushEvaluationStack(EvaluationStack* stack, const EvaluationData* data) {
    if (isFull(stack)) {
        throwError(ERROR_CUSTOM_STACK_OVERFLOW, "Error: trying to push on a full EvaluationStack");
    }
    stack->items[++(stack->top)] = *data;
} 

inline EvaluationData* popEvaluationStack(EvaluationStack* stack) {
    if (isEmpty(stack)) {
        throwError(ERROR_CUSTOM_STACK_UNDERFLOW, "Error: trying to pop from empty EvaluationStack");
    }
    return &stack->items[(stack->top)--];
}

inline const EvaluationData* peekEvaluationStack(const EvaluationStack* stack) {
    if (isEmpty(stack)) {
        throwError(ERROR_CUSTOM_STACK_UNDERFLOW, "Error: trying to peek from empty EvaluationStack");
    }
    return &(stack->items[stack->top]);
}

inline EvaluationData* createEvaluationData(GamestateTreeNode* node, const short state) {
    EvaluationData* data = (EvaluationData*)malloc(sizeof(EvaluationData));
    data->node = node;
    data->state = state;
    return data;
}
