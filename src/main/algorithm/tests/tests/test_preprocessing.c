#include "tests/test_preprocessing.h"

void test_moveGeneration() {
    Queue* queue = queueInit(1);
    initializeTree();

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
    
    short expectedValue = 38;
    int expectedTreeValue = 1;
    char* expected[] = {
        "e4 e5",
        "f4 f5",
        "g2 g4",
        "g2 g3",
        "h2 h4",
        "h2 h3",
        "Ra1xa6",
        "Ra1 a5",
        "Ra1 a4",
        "Ra1 a3",
        "Ra1 a2",
        "Ra1 b1",
        "Rf1 f3",
        "Rf1 f2",
        "Rf1 e1",
        "Nb3 a5",
        "Nb3 c5",
        "Nb3 d4",
        "Nb3 d2",
        "Nc3xb5",
        "Nc3 d5",
        "Nc3 a4",
        "Nc3 a2",
        "Nc3 e2",
        "Nc3 b1",
        "Bd3xb5",
        "Bd3 c4",
        "Bd3 e2",
        "Bc1 e3",
        "Bc1 d2",
        "Qd1 h5",
        "Qd1 g4",
        "Qd1 f3",
        "Qd1 d2",
        "Qd1 e2",
        "Qd1 e1",
        "Kg1 f2",
        "Kg1 h1",

    };

    int actual = calculateMoves(gamestate, queue, NULL);  

    TEST_ASSERT_EQUAL_INT_MESSAGE(expectedValue, actual, "Too many or too little moves generated");
    TEST_ASSERT_EQUAL_INT_MESSAGE(expectedTreeValue, size(tree->head), "Tree is too small or large");
    for (int i = 0; i < actual; i++) {
        char f[20];
        Gamestate* temp = dequeue(queue);
        Move move = temp->move;
        printMove(&move, f);
        //printf("%s\n", f); if debugging is needed
        int result = -1;
        for (int j = 0; j < expectedValue; j++) {
            result = strcmp(f, expected[j]);
            if (result == 0) {
                expected[j] = malloc(strlen("USED") + 1);
                break;
            }
        }    
        TEST_ASSERT_EQUAL_INT_MESSAGE(0, result, f);
    }
    destroyGamestateTreeNodeWithInside(tree->head);
    free(tree);
    tree = NULL;

}

void test_moveGenerationWithOneThread() {
    initializeTree();
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

    int expectedTreeValue = 38+1; //38 moves possible plus the one base move defined above

    MoveGenerationThreadPool* pool = moveGenerationThreadPoolInit(1, 1);    
    pool->workCounter++;
    enqueue(pool->queue, gamestate);

    DWORD threadIDs[pool->maxThreads];
    for (int i = 0; i < pool->maxThreads; i++) {
        pool->threads[i] = CreateThread(
            NULL,   // Default security attributes
            0,      // Default stack size
            generationWorker, // Thread function
            pool,  // argument to thread function
            0,      // Defauzlt creation flags
            &threadIDs[i]    // Ignore thread ID
        );
        
        if (pool->threads[i] == NULL) {
            throwError(ERROR_THREADS_CREATION_FAILED, "Error: failed to create thread for move generation"); 
        }
    }

    DWORD waitResult = WaitForMultipleObjects(pool->maxThreads, pool->threads, TRUE, 50000);
    if (waitResult == WAIT_TIMEOUT) {
        pool->shutdown = true;
        TEST_FAIL_MESSAGE("Some thread took too long");
    }
    TEST_ASSERT_EQUAL_INT_MESSAGE(expectedTreeValue, size(tree->head), "The tree is too small or large");
    TEST_PASS();
    destroyGamestateTreeNodeWithInside(tree->head);
    free(tree);
    tree = NULL;
}

void test_moveGenerationWithThreads() {
    Gamestate *gamestate = gamestateInit();
    //clock_t start, end;
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

    int expectedTreeValue[] = {39, 1516, 57092, 2179984};
    
    for (int depth = 1; depth < 5; depth++) {
        initializeTree();
        MoveGenerationThreadPool* pool = moveGenerationThreadPoolInit(depth, 32);    
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
            switch (depth) {                
                case 1:
                    TEST_FAIL_MESSAGE("Some thread took too long (depth 1)");
                    break;
                case 2:
                    TEST_FAIL_MESSAGE("Some thread took too long (depth 2)");
                    break;
                case 3:
                    TEST_FAIL_MESSAGE("Some thread took too long (depth 3)");
                    break;
                case 4:
                    TEST_FAIL_MESSAGE("Some thread took too long (depth 4)");
                    break;
                default:
                    TEST_FAIL_MESSAGE("Some test took to long");
            }
        }
        //printf("%f\n", ((double)end-start)/CLOCKS_PER_SEC);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expectedTreeValue[depth-1], size(tree->head), "The tree is too small or large");
        destroyGamestateTreeNodeWithInside(tree->head);
        free(tree);
        tree = NULL;
        gamestate->config.parent = NULL;
        gamestate->config.node = NULL;
    }
}

static void __loadTests() {
    RUN_TEST(test_moveGeneration);
    RUN_TEST(test_moveGenerationWithOneThread);
    RUN_TEST(test_moveGenerationWithThreads);
}

void runTests_preprocessing() {
    UNITY_BEGIN();
    __loadTests();
    UNITY_END();
}

void includeTests_preprocessing() {
    UnitySetTestFile(__FILE__);
    __loadTests();
}
