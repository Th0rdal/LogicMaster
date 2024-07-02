#include "tests/test_evaluation.h"

void test_evaluation1() {
    Gamestate *gamestate = gamestateInit();
    // Bitboards
    gamestate->bitboards.pawn =     0x000798400C006300; 
    gamestate->bitboards.rook =     0x8100000000000084;
    gamestate->bitboards.knight =   0x0000240000600000;
    gamestate->bitboards.bishop =   0x2008000000100020;
    gamestate->bitboards.queen =    0x0020000000000010;
    gamestate->bitboards.king =     0x0800000000000002; 

    //color
    gamestate->bitboards.color[1] =    0x000000000C7063B6;
    gamestate->bitboards.color[0] =    0xA92FBC4000000000;

    // occupancy
    gamestate->bitboards.occupancy = gamestate->bitboards.color[0] | gamestate->bitboards.color[1];
    gamestate->flags.isWhiteTurn = true;
    gamestate->move.flags.checkmate = false;
    gamestate->move.flags.check = false;
    gamestate->move.flags.draw = false;
    gamestate->config.depth = 1;

    int expected = -1;
    evaluate(gamestate);
 
    TEST_ASSERT_EQUAL_INT_MESSAGE(expected, gamestate->config.evaluation, "The evaluation does not match");
 
    gamestate->config.depth = 4;
    expected = -4;
    evaluate(gamestate);

    TEST_ASSERT_EQUAL_INT_MESSAGE(expected, gamestate->config.evaluation, "The evaluation does not match");
    
    gamestate->config.depth = 2;
    gamestate->move.flags.check = true;
    expected = -2 + CHECK;

    evaluate(gamestate);

    TEST_ASSERT_EQUAL_INT_MESSAGE(expected, gamestate->config.evaluation, "The evaluation does nto match");
}

void test_evaluation2() {
    int maxThreads = 1;
    int expectedCapture = 0;
    int expectedNonCapture = -1;
    Gamestate *gamestate = gamestateInit();
    // Bitboards
    gamestate->bitboards.pawn =     0x000798400C006300; 
    gamestate->bitboards.rook =     0x8100000000000084;
    gamestate->bitboards.knight =   0x0000240000600000;
    gamestate->bitboards.bishop =   0x2008000000100020;
    gamestate->bitboards.queen =    0x0020000000000010;
    gamestate->bitboards.king =     0x0800000000000002; 

    //color
    gamestate->bitboards.color[1] =    0x000000000C7063B6;
    gamestate->bitboards.color[0] =    0xA92FBC4000000000;

    // occupancy
    gamestate->bitboards.occupancy = gamestate->bitboards.color[0] | gamestate->bitboards.color[1];
    gamestate->flags.isWhiteTurn = false; 

    // initialize tree
    initializeTree();
    MoveGenerationThreadPool* pool = moveGenerationThreadPoolInit(1, 1);    
    pool->workCounter++;
    enqueue(pool->queue, gamestate);
    
    for (int i = 0; i < pool->maxThreads; i++) {
        //start=clock();
        pool->threads[i] = CreateThread(
            NULL,   // Default security attributes
            0,      // Default stack size
            generationWorker, // Thread function
            pool,  // argument to thread function
            0,      // Defauzlt creation flags
            NULL    // Ignore thread ID
        );
        
        if (pool->threads[i] == NULL) {
            throwError(ERROR_THREADS_CREATION_FAILED, "Error: failed to create thread for move generation"); 
        }
    }

    DWORD waitResult = WaitForMultipleObjects(pool->maxThreads, pool->threads, TRUE, 10000);
                                                                                       
    //end = clock();
    if (waitResult == WAIT_TIMEOUT) {
        pool->shutdown = true;
        TEST_FAIL_MESSAGE("Error in creating tree for evaluation");
    }
    //printf("%f\n", ((double)end-start)/CLOCKS_PER_SEC);
    TEST_ASSERT_EQUAL_INT_MESSAGE(39, size(tree->head), "Error in creating tree for evaluation");

    EvaluationThreadPool* evalPool = evaluationThreadPoolInit(maxThreads, tree->head);
    for (int i = 0; i < maxThreads; i++) {
        evalPool->threads[i] = CreateThread(
            NULL,
            0,
            evaluationWorker,
            evalPool,
            0,
            NULL
        );
    }

    waitResult = WaitForMultipleObjects(evalPool->maxThreads, evalPool->threads, TRUE, 50000);
    if (waitResult == WAIT_TIMEOUT) {
        evalPool->shutdown = true;
        throwError(ERROR_THREADS_TIMEOUT, "Error: evaluation thread took too long and triggered the MAX_WAITTIME timeout. Threads were shutdown");
    }

    GamestateTreeBranch* branch = tree->head->children;
    Gamestate* temp;

    while (branch != NULL) {
        temp = branch->node->gamestate;
        if (temp->move.flags.capture) {
            TEST_ASSERT_EQUAL_INT_MESSAGE(expectedCapture, temp->config.evaluation, "Capture evaluation not correct");
        } else {
            TEST_ASSERT_EQUAL_INT_MESSAGE(expectedNonCapture, temp->config.evaluation, "Non caputre evaluation not correct");
        }
        /* for debugging purposes
        char f[20];
        printMove(&temp->move, f);
        printf("%d %s %s\n",
                temp->config.evaluation,
                temp->move.flags.capture?"true":"false",
                f);
        */
        branch = branch->next; 
    }
    evaluateLastGamestate(tree->head);
    evalPool = NULL;
}

static void __loadTests() {
   RUN_TEST(test_evaluation1);
   RUN_TEST(test_evaluation2);
}

void runTests_evaluation() {
    UNITY_BEGIN();
    __loadTests();
    UNITY_END();
}

void includeTests_evaluation() {
    UnitySetTestFile(__FILE__);
    __loadTests();
}
