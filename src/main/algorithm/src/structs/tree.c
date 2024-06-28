#include "structs/tree.h"

#include <windows.h>

GamestateTree* tree;

void initializeTree() {
    tree = (GamestateTree*)malloc(sizeof(GamestateTree));
    tree->head = NULL; 
    InitializeCriticalSection(&tree->lock);
} 

int size(GamestateTreeNode* node) {
    if (node->children == NULL) {
        return 1;
    }
    GamestateTreeBranch* branch = node->children;
    int counter = 1;
    while (branch != NULL) {
        counter += size(branch->node);
        branch = branch->next;
    }
    return counter;
}

GamestateTreeNode* createGamestateTreeNode(Gamestate* gamestate) {
    GamestateTreeNode* node = (GamestateTreeNode*)malloc(sizeof(GamestateTreeNode));
    if (node == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for a GamestateTree node");
    }

    node->gamestate = gamestate;
    node->children = NULL;
    gamestate->config.node = node;
    
    GamestateTreeNode* parent = gamestate->config.parent;
    if (parent != NULL) {
        GamestateTreeBranch* branch = (GamestateTreeBranch*)malloc(sizeof(GamestateTreeBranch));
        if (branch == NULL) {
            throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for a GamestateTreeBranch");
        }
        branch->prev = NULL;
        branch->next = parent->children;
        branch->node = node;
        if (parent->children != NULL) {
            parent->children->prev = branch;
        }
        parent->children = branch;
    } else { 
        // This should only be executed when using the function with criticalCreateGamestateTreeNode
        // as this should only happen once when the first gamestate is added.
        tree->head = node;
    }
    //printf("size %d %d %d %p %p\n", size(tree->head), i, GetCurrentThreadId(), node, gamestate);
    return node;
}

GamestateTreeNode* criticalCreateGamestateTreeNode(Gamestate* gamestate) {
    EnterCriticalSection(&tree->lock);
    GamestateTreeNode* temp = createGamestateTreeNode(gamestate);
    LeaveCriticalSection(&tree->lock);
    return temp;
}

inline void destroyGamestateTreeNodeWithInside(GamestateTreeNode* node) {
    return;
    if (node == NULL) {
        return;
    }
    GamestateTreeBranch* branch = node->children;
    GamestateTreeBranch* tempBranch = NULL;
    while (branch != NULL) {
        tempBranch = branch->next;
        destroyGamestateTreeBranchWithInside(branch);    
        branch = tempBranch;

    }
    if (node->gamestate != NULL) {
        destroyGamestate(node->gamestate);
    }
    free(node);
}

inline void destroyGamestateTreeBranchWithInside(GamestateTreeBranch* branch) {
    return;
    if (branch == NULL)  {
        return;
    }
    if (branch->prev != NULL) {
        branch->prev->next = branch->next;
    }
    if (branch->next != NULL) {
        branch->next->prev = branch->prev;
    }
    destroyGamestateTreeNodeWithInside(branch->node);
    free(branch);
}
