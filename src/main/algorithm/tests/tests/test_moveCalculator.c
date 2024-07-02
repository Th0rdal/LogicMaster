#include "tests/test_moveCalculator.h"

void test_kingMove() {
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
    gamestate->flags.kCastle[0] = true;

    Position position1;
    position1.rank = 1;
    position1.file = 7;

    Position position2;
    position2.rank = 8;
    position2.file = 5;

    uint64_t expected1 = 0x0000000000000401;
    uint64_t expected2 = 0x1610000000000000;
    uint64_t actual1 = kingMoves(gamestate, &position1);

    gamestate->flags.isWhiteTurn = true;
    uint64_t actual2 = kingMoves(gamestate, &position2);

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected1, actual1, "white king G1");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected2, actual2, "black king E8");
    
}


void test_queenMove() {
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

    // occupa->cy
    gamestate->bitboards.occupancy = gamestate->bitboards.color[0] | gamestate->bitboards.color[1];

    gamestate->flags.isWhiteTurn = false;

    Position position1;
    position1.rank = 1;
    position1.file = 4;

    Position position2;
    position2.rank = 7;
    position2.file = 3;

    uint64_t expected1 = 0x0000000102041808;
    uint64_t expected2 = 0x50D0408000000000;

    uint64_t actual1 = queenMoves(gamestate, &position1);

    gamestate->flags.isWhiteTurn = true;

    uint64_t actual2 = queenMoves(gamestate, &position2);

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected1, actual1, "white queen D1");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected2, actual2, "black queen C7");
    
}

void test_knightMove() {
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

    // occupa->cy
    gamestate->bitboards.occupancy = gamestate->bitboards.color[0] | gamestate->bitboards.color[1];

    gamestate->flags.isWhiteTurn = false;

    Position position1;
    position1.rank = 3;
    position1.file = 2;

    Position position2;
    position2.rank = 3;
    position2.file = 3;

    Position position3;
    position3.rank = 6;
    position3.file = 3;

    Position position4;
    position4.rank = 6;
    position4.file = 6;

    uint64_t expected1 = 0x000000A010001000;
    uint64_t expected2 = 0x0000005080008840;
    uint64_t expected3 = 0x5080008850000000;
    uint64_t expected4 = 0x021000110A000000;

    uint64_t actual1 = knightMoves(gamestate, &position1);
    uint64_t actual2 = knightMoves(gamestate, &position2);

    gamestate->flags.isWhiteTurn = true;

    uint64_t actual3 = knightMoves(gamestate, &position3);
    uint64_t actual4 = knightMoves(gamestate, &position4);

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected1, actual1, "white knight B3");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected2, actual2, "white knight C3");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected3, actual3, "black knight C6");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected4, actual4, "black knight F6");
    
}
 
void test_bishopMove() {
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

    // occupa->cy
    gamestate->bitboards.occupancy = gamestate->bitboards.color[0] | gamestate->bitboards.color[1];

    gamestate->flags.isWhiteTurn = false;

    Position position1;
    position1.rank = 1;
    position1.file = 3;

    Position position2;
    position2.rank = 3;
    position2.file = 4;

    Position position3;
    position3.rank = 8;
    position3.file = 3;

    Position position4;
    position4.rank = 7;
    position4.file = 5;

    uint64_t expected1 = 0x0000000000081000;
    uint64_t expected2 = 0x0000004020000800;
    uint64_t expected3 = 0x0050000000000000;
    uint64_t expected4 = 0x1400000000000000;

    uint64_t actual1 = bishopMoves(gamestate, &position1);
    uint64_t actual2 = bishopMoves(gamestate, &position2);

    gamestate->flags.isWhiteTurn = true;

    uint64_t actual3 = bishopMoves(gamestate, &position3);
    uint64_t actual4 = bishopMoves(gamestate, &position4);

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected1, actual1, "white bishop C1");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected2, actual2, "white bishop D3");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected3, actual3, "black bishop C8");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected4, actual4, "black bishop E7");
    
}

  
void test_rookMove() {
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

    // occupa->cy
    gamestate->bitboards.occupancy = gamestate->bitboards.color[0] | gamestate->bitboards.color[1];

    gamestate->flags.isWhiteTurn = false;

    Position position1;
    position1.rank = 1;
    position1.file = 1;

    Position position2;
    position2.rank = 1;
    position2.file = 6;

    Position position3;
    position3.rank = 8;
    position3.file = 1;

    Position position4;
    position4.rank = 8;
    position4.file = 8;

    uint64_t expected1 = 0x0000808080808040;
    uint64_t expected2 = 0x0000000000040408;
    uint64_t expected3 = 0x4080000000000000;
    uint64_t expected4 = 0x0600000000000000;

    uint64_t actual1 = rookMoves(gamestate, &position1);
    uint64_t actual2 = rookMoves(gamestate, &position2);

    gamestate->flags.isWhiteTurn = true;

    uint64_t actual3 = rookMoves(gamestate, &position3);
    uint64_t actual4 = rookMoves(gamestate, &position4);

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected1, actual1, "white rook A1");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected2, actual2, "white rook F1");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected3, actual3, "black rook A8");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected4, actual4, "black rook H8");
    
}

void test_pawnMove_enPassant() {
    Gamestate *gamestate1 = gamestateInit();
    gamestate1->bitboards.pawn = 0x0000006000000000;
    gamestate1->bitboards.color[1] = 0x0000002000000000;
    gamestate1->bitboards.color[0] = 0x0000004000000000;
    gamestate1->bitboards.occupancy = 0x0000006000000000;
    gamestate1->flags.isWhiteTurn = false;
    gamestate1->flags.canEnPassant = true;
    gamestate1->enPassantPosition.rank = 6;
    gamestate1->enPassantPosition.file = 2;

    Position position1;
    position1.rank = 5;
    position1.file = 3;

    Gamestate *gamestate2= gamestateInit();
    gamestate2->bitboards.pawn = 0x0000006000000000;
    gamestate2->bitboards.color[1] = 0x0000002000000000;
    gamestate2->bitboards.color[0]= 0x0000004000000000;
    gamestate2->bitboards.occupancy = 0x0000006000000000;
    gamestate2->flags.isWhiteTurn = true;
    gamestate2->flags.canEnPassant = true;
    gamestate2->enPassantPosition.rank = 3;
    gamestate2->enPassantPosition.file = 2;

    Position position2;
    position2.rank = 4;
    position2.file = 3;

    uint64_t expected1 = 0x0000600000000000;
    uint64_t expected2 = 0x0000000000600000;

    uint64_t actual1 = pawnMoves(gamestate1, &position1);
    uint64_t actual2 = pawnMoves(gamestate2, &position2);

    TEST_ASSERT_EQUAL_UINT64(expected1, actual1);
    TEST_ASSERT_EQUAL_UINT64(expected2, actual2);
}

void test_pawnMove_black() {
    Gamestate *gamestate = gamestateInit();
    gamestate->bitboards.pawn =     0x00FF15A00000FF00;
    gamestate->bitboards.rook =     0x8100000000000081;
    gamestate->bitboards.knight =   0x4200000000000042;
    gamestate->bitboards.bishop =   0x2400000000000024;
    gamestate->bitboards.queen =    0x1000000000000010;
    gamestate->bitboards.king =     0x0800000000000008;

    gamestate->bitboards.color[0] =    0xFFFF000000000000;
    gamestate->bitboards.color[1] =    0x000015A00000FFFF;

    gamestate->bitboards.occupancy = 0xFFFF15A00000FFFF;

    gamestate->enPassantPosition.rank = 0;
    gamestate->enPassantPosition.file = 0;

    gamestate->counters.fullMove = 1;
    gamestate->counters.halfMove = 0;

    gamestate->flags.isWhiteTurn = true;
    gamestate->flags.canEnPassant = false;
   
    Position position1; //A2 move 2 up, capture right. nothing left
    position1.rank = 7;
    position1.file = 1;

    Position position2; //B2 not move or capture
    position2.rank = 7;
    position2.file = 2;

    Position position3; //C2 move up 2 capture left right
    position3.rank = 7;
    position3.file = 3;

    Position position4; //D2 no movement
    position4.rank = 7;
    position4.file = 4;

    Position position5; //E2 move 1 up caputre left
    position5.rank = 7;
    position5.file = 5;

    Position position6; //F2 move 2 up
    position6.rank = 7;
    position6.file = 6;

    Position position7; //G2 move 1 up capture right enPassant possible
    position7.rank = 7;
    position7.file = 7;

    Position position8; //H2 no movement
    position8.rank = 7;
    position8.file = 8;

    uint64_t actual1 = pawnMoves(gamestate, &position1);
    uint64_t actual2 = pawnMoves(gamestate, &position2);
    uint64_t actual3 = pawnMoves(gamestate, &position3);
    uint64_t actual4 = pawnMoves(gamestate, &position4);
    uint64_t actual5 = pawnMoves(gamestate, &position5);
    uint64_t actual6 = pawnMoves(gamestate, &position6);
    uint64_t actual7 = pawnMoves(gamestate, &position7);
    uint64_t actual8 = pawnMoves(gamestate, &position8);

    uint64_t expected1 = 0x0000800000000000;
    uint64_t expected2 = 0x0000404000000000;
    uint64_t expected3 = 0x0000300000000000;
    uint64_t expected4 = 0x0000000000000000;
    uint64_t expected5 = 0x00001C0800000000;
    uint64_t expected6 = 0x0000000000000000;
    uint64_t expected7 = 0x0000070200000000;
    uint64_t expected8 = 0x0000000000000000;

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected1, actual1, "position 1");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected2, actual2, "position 2");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected3, actual3, "position 3");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected4, actual4, "position 4");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected5, actual5, "position 5");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected6, actual6, "position 6");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected7, actual7, "position 7");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected8, actual8, "position 8");
}

void test_pawnMove_white() {
    Gamestate *gamestate = gamestateInit();
    gamestate->bitboards.pawn =     0x00FF00000A51FF00;
    gamestate->bitboards.rook =     0x8100000000000081;
    gamestate->bitboards.knight =   0x4200000000000042;
    gamestate->bitboards.bishop =   0x2400000000000024;
    gamestate->bitboards.queen =    0x1000000000000010;
    gamestate->bitboards.king =     0x0800000000000008;

    gamestate->bitboards.color[0] =    0xFFFF00000A510000;
    gamestate->bitboards.color[1] =    0x000000000000FFFF;

    gamestate->bitboards.occupancy = 0xFFFF00000A51FFFF;

    gamestate->enPassantPosition.rank = 0;
    gamestate->enPassantPosition.file = 0;

    gamestate->counters.fullMove = 1;
    gamestate->counters.halfMove = 0;

    gamestate->flags.isWhiteTurn = false;
    gamestate->flags.canEnPassant = false;
 
    Position position1; //A2 move 2 up, capture right. nothing left
    position1.rank = 2;
    position1.file = 1;

    Position position2; //B2 not move or capture
    position2.rank = 2;
    position2.file = 2;

    Position position3; //C2 move up 2 capture left right
    position3.rank = 2;
    position3.file = 3;

    Position position4; //D2 no movement
    position4.rank = 2;
    position4.file = 4;

    Position position5; //E2 move 1 up caputre left
    position5.rank = 2;
    position5.file = 5;

    Position position6; //F2 move 2 up
    position6.rank = 2;
    position6.file = 6;

    Position position7; //G2 move 1 up capture right enPassant possible
    position7.rank = 2;
    position7.file = 7;

    Position position8; //H2 no movement
    position8.rank = 2;
    position8.file = 8;

    uint64_t actual1 = pawnMoves(gamestate, &position1);
    uint64_t actual2 = pawnMoves(gamestate, &position2);
    uint64_t actual3 = pawnMoves(gamestate, &position3);
    uint64_t actual4 = pawnMoves(gamestate, &position4);
    uint64_t actual5 = pawnMoves(gamestate, &position5);
    uint64_t actual6 = pawnMoves(gamestate, &position6);
    uint64_t actual7 = pawnMoves(gamestate, &position7);
    uint64_t actual8 = pawnMoves(gamestate, &position8);

    uint64_t expected1 = 0x0000000080C00000;
    uint64_t expected2 = 0x0000000000000000;
    uint64_t expected3 = 0x0000000020700000;
    uint64_t expected4 = 0x0000000000000000;
    uint64_t expected5 = 0x0000000000180000;
    uint64_t expected6 = 0x0000000004040000;
    uint64_t expected7 = 0x0000000000030000;
    uint64_t expected8 = 0x0000000000000000;

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected1, actual1, "position 1");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected2, actual2, "position 2");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected3, actual3, "position 3");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected4, actual4, "position 4");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected5, actual5, "position 5");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected6, actual6, "position 6");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected7, actual7, "position 7");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected8, actual8, "position 8");
}

void test_squareAttacked() {
    Gamestate *gamestate = gamestateInit();
    gamestate->bitboards.pawn =     0x0000001800000000;
    gamestate->bitboards.rook =     0xBF02020202020002;
    gamestate->bitboards.knight =   0x0000000040000000;
    gamestate->bitboards.bishop =   0x000000000000005C;
    gamestate->bitboards.queen =    0x4000000000000000;
    gamestate->bitboards.king =     0x0001000000004000;

    gamestate->bitboards.color[0] =    0xFF03021A4202005E;
    gamestate->bitboards.color[1] =    0x0000000000004000;

    gamestate->bitboards.occupancy = gamestate->bitboards.color[0] | gamestate->bitboards.color[1];
    gamestate->flags.isWhiteTurn = true;
    Position position1 = {2, 2};

    uint64_t expected1 = 0;
    uint64_t expected2 = 0x4000000000000000;
        
    uint64_t actual1 = squareAttacked(gamestate, &position1, true);
    
    gamestate->bitboards.knight = 0x0000000000000000;
    gamestate->bitboards.color[0] = 0xFF03021A0202005E;
    gamestate->bitboards.occupancy = gamestate->bitboards.color[0] | gamestate->bitboards.color[1];

    uint64_t actual2 = squareAttacked(gamestate, &position1, true);

    TEST_ASSERT_EQUAL_INT_MESSAGE(expected1, actual1, "no square attacking");
    TEST_ASSERT_EQUAL_INT_MESSAGE(expected2, actual2, "1 attacking square");
}

void __loadTests() {
    RUN_TEST(test_pawnMove_white);
    RUN_TEST(test_pawnMove_black);
    RUN_TEST(test_pawnMove_enPassant);
    RUN_TEST(test_rookMove);
    RUN_TEST(test_bishopMove);
    RUN_TEST(test_knightMove);
    RUN_TEST(test_queenMove);
    RUN_TEST(test_kingMove);
    RUN_TEST(test_squareAttacked);
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
