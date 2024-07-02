#include "structs/stack.h"

inline EvaluationStack* initializeStack() {
    EvaluationStack* stack = (EvaluationStack*)malloc(sizeof(EvaluationStack));
    if (stack == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for stack");
    }
    stack->top = -1;
    stack->MAX_STACK_SIZE = 10;
    stack->items = (EvaluationData**)malloc(stack->MAX_STACK_SIZE * sizeof(EvaluationData*));
    if (stack == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for stack data");
    }
    return stack;
}

