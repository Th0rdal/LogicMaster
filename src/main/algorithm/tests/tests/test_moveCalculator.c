#include "tests/test_moveCalculator.h"

void test_antiDiagonalSlide() {
    Gamestate gamestate = gamestateInit();
    gamestate.bitboards.occupancy = 0xBFBF60000002FFFB;
    gamestate.flags.isWhiteTurn = true;
    gamestate.bitboards.white = 0x000000000002FFFB;
    short pos = 9;

    uint64_t expected1 = 0x0000201008040000;
    uint64_t expected2 = 0x0000000000010004;
    uint64_t actual1 = antiDiagonalSlide(&gamestate, pos);
    uint64_t actual2 = diagonalSlide(&gamestate, pos);
    TEST_ASSERT_EQUAL_UINT64(expected1, actual1);
    TEST_ASSERT_EQUAL_UINT64(expected2, actual2);
}

void __loadTests() {
    RUN_TEST(test_antiDiagonalSlide);
}

void runTests_moveCalculator() {
    UNITY_BEGIN();
    __loadTests();
    UNITY_END();
}

void includeTests_moveCalculator() {
    UnitySetTestFile(__FILE__);
    __loadTests();
}
