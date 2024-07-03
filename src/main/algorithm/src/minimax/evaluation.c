#include "minimax/evaluation.h"

static GamestateTreeNode* getGamestateTreeNode(EvaluationThreadPool* pool);

/*!
 * evaluates a specific gamestate based on an algorithm.
 * Algorithm counts piece values for each sides and subtracts white pieces from black pieces
 * draw is evaluated as 0, checkmate is + or - 100
 *
 * @param gamestate: the gamestate to evaluate
 *
 * */
void evaluate(Gamestate* gamestate) {
    int eval = 0;
    short depth = gamestate->config.depth;

    if (gamestate->move.flags.draw) {
        gamestate->config.evaluation = 0;
        return;
    } else if (gamestate->move.flags.checkmate) {
        eval += gamestate->flags.isWhiteTurn ? CHECKMATE : -CHECKMATE;
        eval *= depth;
        return;
    } else if (gamestate->move.flags.check) {
        eval += gamestate->flags.isWhiteTurn ? CHECK : -CHECK;
    }

    if (gamestate->move.flags.kCastle[gamestate->flags.isWhiteTurn ? 1 : 0] || gamestate->move.flags.kCastle[gamestate->flags.isWhiteTurn ? 1 : 0]) {
        eval += (CASTLE * depth);
    }

    int whitePawns = __builtin_popcountll(gamestate->bitboards.pawn & gamestate->bitboards.color[1]) * PAWNVALUE;
    int whiteRook = __builtin_popcountll(gamestate->bitboards.rook & gamestate->bitboards.color[1]) * ROOKVALUE;
    int whiteKnight = __builtin_popcountll(gamestate->bitboards.knight & gamestate->bitboards.color[1]) * KNIGHTVALUE;
    int whiteBishop = __builtin_popcountll(gamestate->bitboards.bishop & gamestate->bitboards.color[1]) * BISHOPVALUE;
    int whiteQueen = __builtin_popcountll(gamestate->bitboards.queen & gamestate->bitboards.color[1]) * QUEENVALUE;

    int blackPawns = __builtin_popcountll(gamestate->bitboards.pawn & gamestate->bitboards.color[0]) * PAWNVALUE;
    int blackRook = __builtin_popcountll(gamestate->bitboards.rook & gamestate->bitboards.color[0]) * ROOKVALUE;
    int blackKnight = __builtin_popcountll(gamestate->bitboards.knight & gamestate->bitboards.color[0]) * KNIGHTVALUE;
    int blackBishop = __builtin_popcountll(gamestate->bitboards.bishop & gamestate->bitboards.color[0]) * BISHOPVALUE;
    int blackQueen = __builtin_popcountll(gamestate->bitboards.queen & gamestate->bitboards.color[0]) * QUEENVALUE;

    int whitePieces = whitePawns + whiteRook + whiteKnight + whiteBishop + whiteQueen;
    int blackPieces = -1 * (blackPawns + blackRook + blackKnight + blackBishop + blackQueen);

    eval += (whitePieces + blackPieces) * depth;
    gamestate->config.evaluation = eval;
}

/*!
 * goes through each child of the base node and evaluates the nodes.
 * It does this by going further into the tree until a node with no children is found.
 * It evaluates that node and then goes up the stack
 * If more than 1 child have the same evaluation and it is the best, the progarm will only save the first gamestate 
 * Since no further calculations are done, it does not matter what gamestate will be taken as only the evaluation matters
 */
DWORD WINAPI evaluationWorker(LPVOID lpParam) {
    EvaluationThreadPool* pool = (EvaluationThreadPool *)lpParam;
    EvaluationStack* stack = initializeStack();
    GamestateTreeNode* node = getGamestateTreeNode(pool);
    while (node != NULL) {

        GamestateTreeBranch* branch = node->children;
        EvaluationData* data;
        GamestateTreeNode* gamestateTreeNode;

        pushEvaluationStack(stack, createEvaluationData(node, 0));
        while (!isEmpty(stack)) {
            data = popEvaluationStack(stack);
            gamestateTreeNode = data->node;

            if (data->state == 0) { // children not calculated yet
                if (node->children == NULL) { // if it has no children, evaluate it and dont add it to stack again
                   evaluate(gamestateTreeNode->gamestate);
                   free(data);
                   continue;
                }

                // happens if node has children
                // add node with state 1 (children calculated)
                // then add children with node 0 (children calculated)
                pushEvaluationStack(stack, createEvaluationData(gamestateTreeNode, 1));

                branch = gamestateTreeNode->children;
                while (branch != NULL) {
                    pushEvaluationStack(stack, createEvaluationData(branch->node, 0));
                    branch = branch->next;
                }
            } else if (data->state == 1) { // children calculated
                Gamestate* gamestate = gamestateTreeNode->gamestate;
                int bestEvaluation = gamestate->flags.isWhiteTurn ? INT_MIN : INT_MAX;
                int bestAverageEval = bestEvaluation;
                bool useMax = gamestate->flags.isWhiteTurn ? 1 : 0;
                GamestateTreeBranch* branch = gamestateTreeNode->children;
                int averageEval = 0;
                int counter = 0;
                
                while (branch != NULL) { // loop through branches and find best eval
                    int evaluation = branch->node->gamestate->config.evaluation;
                    averageEval += evaluation;
                    counter++;
                    if ((useMax && evaluation > bestEvaluation) || (!useMax && evaluation < bestEvaluation)) {
                        bestEvaluation = evaluation;
                        bestAverageEval = branch->node->gamestate->config.averageEvaluation;
                    } else if (evaluation == bestEvaluation) {
                        if ((useMax && averageEval > bestAverageEval) || (!useMax && averageEval < bestAverageEval)) {
                            bestEvaluation = evaluation;
                            bestAverageEval = branch->node->gamestate->config.averageEvaluation;
                        }
                    }
                    branch = branch->next;
                }
                
                gamestateTreeNode->gamestate->config.evaluation = bestEvaluation;
                counter = counter == 0 ? 1 : counter;
                gamestateTreeNode->gamestate->config.averageEvaluation = averageEval / counter;
            }
            free(data); // free the no longer needed EvaluationData struct. no node with no children should ever be here
        }
        node = getGamestateTreeNode(pool);
    }
    return 0;
} 

EvaluationThreadPool* evaluationThreadPoolInit(command_args* args, GamestateTreeNode* srcTree) {
    EvaluationThreadPool* pool = (EvaluationThreadPool*)malloc(sizeof(EvaluationThreadPool));
    if (pool == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for evaluationThreadPool");
    }
    InitializeCriticalSection(&pool->lock);
    pool->maxThreads = args->maxThreads;
    pool->threads = (HANDLE*)malloc(args->maxThreads * sizeof(HANDLE));
    if (pool->threads == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for evaluation threads");
    }
    pool->shutdown = false;
    pool->workCounter = 0;
    pool->srcTree = srcTree;
    return pool;
}  

Gamestate* evaluateLastGamestate(GamestateTreeNode* gamestateTreeNode) {
    Gamestate* gamestate = gamestateTreeNode->gamestate;
    int bestEvaluation = gamestate->flags.isWhiteTurn ? INT_MIN : INT_MAX;
    int bestAverageEval = bestEvaluation;
    Gamestate* chosen = NULL;
    bool useMax = gamestate->flags.isWhiteTurn ? 1 : 0;
    GamestateTreeBranch* branch = gamestateTreeNode->children;
    int averageEval = 0;
    int counter = 0;
    
    while (branch != NULL) { // loop through branches and find best eval
        int evaluation = branch->node->gamestate->config.evaluation;
        averageEval += evaluation;
        counter++;
        if ((useMax && evaluation > bestEvaluation) || (!useMax && evaluation < bestEvaluation)) {
            bestEvaluation = evaluation;
            bestAverageEval = branch->node->gamestate->config.averageEvaluation;
            chosen = branch->node->gamestate;
        } else if (evaluation == bestEvaluation) {
            if ((useMax && averageEval > bestAverageEval) || (!useMax && averageEval < bestAverageEval)) {
                bestEvaluation = evaluation;
                bestAverageEval = branch->node->gamestate->config.averageEvaluation;
                chosen = branch->node->gamestate;
            }
        }
        branch = branch->next;
    }
    
    return chosen;
}

/*!
 * gets a GamestateTreeNode that is a child of the head node and returns it. Critical section, so a 
 * lock is used. If all chilren were already fetched, it returns NULL
 * 
 * @param pool: the thread pool configuration struct
 *
 * @return: GamestateTreeNode pointer if there are still children to process, else NULL
 *
 * */
static GamestateTreeNode* getGamestateTreeNode(EvaluationThreadPool* pool) {
    EnterCriticalSection(&pool->lock);

    GamestateTreeBranch* branch = pool->srcTree->children;
    for (int i = 0; i < pool->workCounter; i++) {
        branch = branch->next; 
        if (branch == NULL) {
            LeaveCriticalSection(&pool->lock);
            return NULL;
        }
    }
    pool->workCounter++;
    LeaveCriticalSection(&pool->lock);
    return branch->node;
}

void destroyEvaluationThreadPool(EvaluationThreadPool* pool) {
    DeleteCriticalSection(&pool->lock);
    free(pool->threads);
    free(pool);
}

void evaluation_start(command_args* args) {
    EvaluationThreadPool* evalPool = evaluationThreadPoolInit(args, tree->head);
    for (int i = 0; i < args->maxThreads; i++) {
        evalPool->threads[i] = CreateThread(
            NULL,
            0,
            evaluationWorker,
            evalPool,
            0,
            NULL
        );
    }

    DWORD waitResult = WaitForMultipleObjects(evalPool->maxThreads, evalPool->threads, TRUE, MAX_WAITTIME);
    if (waitResult == WAIT_TIMEOUT) {
        evalPool->shutdown = true;
        throwError(ERROR_THREADS_TIMEOUT, "Error: evaluation thread took too long and triggered the MAX_WAITTIME timeout. Threads were shutdown");
    }
    destroyEvaluationThreadPool(evalPool);
    evalPool = NULL;
} 
