#include "tests/test_preprocessing.h"

void test_moveGeneration() {
    Queue queue = queueInit(); 

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
    
    short expectedValue = 38;
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

    int actual = calculateMoves(gamestate, &queue);  

    TEST_ASSERT_EQUAL_INT_MESSAGE(expectedValue, actual, "Too many or too little moves generated");
    for (int i = 0; i < actual; i++) {
        char f[20];
        Gamestate* temp = dequeue(&queue);
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

}

static void __loadTests() {
    RUN_TEST(test_moveGeneration);
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
