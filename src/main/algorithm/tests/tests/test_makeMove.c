#include "tests/test_makeMove.h"

void test_makeMove() {
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
 
    Position start = positionInit();
    start.file = 5;
    start.rank = 4;

    Position end = positionInit();
    end.file = 5;
    end.rank = 5;

    Gamestate* newGamestate = gamestateInit();
    bool value = gamestate_makeMove(gamestate, newGamestate, PAWN, &start, &end);
    TEST_ASSERT_TRUE(value);

}

static void __loadTests() {
    RUN_TEST(test_makeMove);
}

void runTests_makeMove() {
    UNITY_BEGIN();
    __loadTests();
    UNITY_END();
}

void includeTests_makeMove() {
    UnitySetTestFile(__FILE__);
    __loadTests();
}
