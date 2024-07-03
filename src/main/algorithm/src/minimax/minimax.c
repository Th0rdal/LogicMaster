#include "minimax/minimax.h"

/*!
 * gets the number of processors for maxThreads
 *
 *  @return: maxThreads
 */
static inline int setupMaxThreads(int maxThreads) {
    if (maxThreads > 0) {
        return maxThreads;
    }
    SYSTEM_INFO sysinfo;
    GetSystemInfo(&sysinfo);
    int maxThreadsCalc = sysinfo.dwNumberOfProcessors;
    if (maxThreadsCalc < 1) {
        throwWarning(WARNING_COULD_NOT_RETRIEVE_CORE_NUMBER, "Warning: failed to retrieve the amount of cores the processor has for max worker threads. Set max worker threads to 1");
        maxThreadsCalc = 1;
    }
    return maxThreadsCalc;
}

Gamestate* minimax(command_args* args, Gamestate* gamestate) {
    args->maxThreads = setupMaxThreads(args->maxThreads);
   
    initializeTree();
    preprocessing_start(args, gamestate);
    evaluation_start(args);
    return evaluateLastGamestate(tree->head);
}

void printAllPossibleMoves(command_args* args, Gamestate* gamestate) {
    if (tree->head == NULL) {
        minimax(args, gamestate);
    }
    
    GamestateTreeNode* node;
    GamestateTreeBranch* branch;
    
    if (tree->head->gamestate == gamestate) {
        node = tree->head;
    } else {
        node = searchGamestateInTree(tree->head, gamestate);
    }

    branch = node->children;
    while (branch != NULL) {
        char move[20];
        printMove(&branch->node->gamestate->move, move);
        printf("%s\n", move);
        branch = branch->next;
    }
}
