#ifndef TREE_H
#define TREE_H

#include <windows.h>

#include "structs/gamestate.h"
#include "errors.h"

typedef struct GamestateTreeNode GamestateTreeNode;
typedef struct GamestateTreeBranch GamestateTreeBranch;

typedef struct GamestateTreeBranch {
    GamestateTreeBranch* next;
    GamestateTreeBranch* prev;
    GamestateTreeNode* node;
} GamestateTreeBranch;

typedef struct GamestateTreeNode {
    struct Gamestate* gamestate;
    GamestateTreeBranch* children;
} GamestateTreeNode;

typedef struct {
    GamestateTreeNode* head;
    CRITICAL_SECTION lock;
} GamestateTree;

// initialize tree needs to be called before this variable can be used correctly
extern GamestateTree* tree;

/*!
 * initializes the GamestateTree. This needs to be called before the tree variable can be used
 *
 * */
void initializeTree();

/*!
 * initialize a GamestateTree node and fill it with the gamestate. Also adds it to children of the parent gamestate given
 * in the config of gamestate
 *
 * @param gamestate: the gamestate to create the GamestateTree node of
 *
 * @return: the GamestateTreeNode that was just created*
 * */
GamestateTreeNode* createGamestateTreeNode(Gamestate* gamestate);

/*!
 * destroys a GamestateTreeNode struct together with all structs that are saved within recursively.
 * That means all children of this node will be destroyed as well
 *
 * @param node: pointer to the node to destroy
 *
 * */
void destroyGamestateTreeNodeWithInside(GamestateTreeNode* node);

/*!
 * destroys a GamestateTreeBranch struct together with all structs that are saved within. It also 
 * sets the prev and next pointer of the previous and next branch to point to each other.
 *
 * @param branch: pointer to the branch to destroy
 *
 * */
void destroyGamestateTreeBranchWithInside(GamestateTreeBranch* branch);

/*!
 * creates a Critical Section for createGamestateTreeNode. This needs to be used when adding the gamestate after
 * getting it from the dequeue function as other threads might get a gamestate with the same parent
 *
 * @param gamestate: the gamestate to create the GamestateTree node of
 *
 * @return: the node that was just created
 * */
GamestateTreeNode* criticalCreateGamestateTreeNode(struct Gamestate* gamestate);

/*!
 * A recursive function that calculates the size of the tree, starting with the node given.
 * The counting starts with the node given as base.
 *
 * @param node: the base node for the calculations
 *
 * @return: the amount of nodes in the tree
 *
 * */
int size(GamestateTreeNode* node);

/*!
 * A recursive function that seraches through the tree to find the given gamestate and return its tree node
 * CAREFUL: comparison is by pointer!!!
 *
 * @param node: the GamestateTreeNode to check
 * @param gamestate: the gamestate to search
 *
 * @return: The GamestateTreeNode pointer to the node containing the gamestate. If the gamestate was not found, it will return NULL
 *  */
GamestateTreeNode* searchGamestateInTree(GamestateTreeNode* node, Gamestate* gamestate);
#endif
