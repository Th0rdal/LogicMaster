#include "tests/test_fenToBitboard.h"

void test_converter_fenToBitboard_workingInput1(void) {
    Gamestate gamestate = gamestateInit();
    char* fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    fenToBitboard(fen, &gamestate);

    // Bitboards
    uint64_t pawn =     0x00FF00000000FF00;
    uint64_t rook =     0x8100000000000081;
    uint64_t knight =   0x4200000000000042;
    uint64_t bishop =   0x2400000000000024;
    uint64_t queen =    0x1000000000000010;
    uint64_t king =     0x0800000000000008;

    //color
    uint64_t black =    0xFFFF000000000000;
    uint64_t white =    0x000000000000FFFF;

    // occupancy
    uint64_t occupancy =0xFFFF00000000FFFF;

    int positionRank = 0;
    int positionFile = 0;

    int fullMove = 1;
    int halfMove = 0;

    // assert
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(pawn, gamestate.bitboards.pawn, "pawn bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(rook, gamestate.bitboards.rook, "rook bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(knight, gamestate.bitboards.knight, "knight bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(bishop, gamestate.bitboards.bishop, "bishop bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(queen, gamestate.bitboards.queen, "queen bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(king, gamestate.bitboards.king, "king bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(occupancy, gamestate.bitboards.occupancy, "occupancy bitboard");

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(white, gamestate.bitboards.color[1], "white bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(black, gamestate.bitboards.color[0], "black bitboard");

    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.isWhiteTurn, "isWhiteTurn");
    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.kCastle[1], "white king castle");
    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.qCastle[1], "white queen castle");
    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.kCastle[0], "black king castle");
    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.kCastle[0], "black queen castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.canEnPassant, "can en passant");

    TEST_ASSERT_EQUAL_INT_MESSAGE(positionRank, gamestate.enPassantPosition.rank, "en passant rank");
    TEST_ASSERT_EQUAL_INT_MESSAGE(positionFile, gamestate.enPassantPosition.file, "en passant file");

    TEST_ASSERT_EQUAL_INT_MESSAGE(fullMove, gamestate.counters.fullMove, "full move counter");
    TEST_ASSERT_EQUAL_INT_MESSAGE(halfMove, gamestate.counters.halfMove, "half move counter");

}

void test_converter_fenToBitboard_workingInput2(void) {
    Gamestate gamestate = gamestateInit();
    char* fen = "rnbq1bnr/ppppkppp/8/4p3/3P4/2N5/PPP1PPPP/1RBQKBNR b K - 3 3"; 
        
    fenToBitboard(fen, &gamestate);

    // Bitboards
    uint64_t pawn =     0x00F700081000EF00;
    uint64_t rook =     0x8100000000000041;
    uint64_t knight =   0x4200000000200002;
    uint64_t bishop =   0x2400000000000024;
    uint64_t queen =    0x1000000000000010;
    uint64_t king =     0x0008000000000008;

    //color
    uint64_t white =    0x000000001020EF7F; 
    uint64_t black =    0xF7FF000800000000;

    // occupancy
    uint64_t occupancy = pawn | rook | knight | bishop | queen | king;

    int positionRank = 0;
    int positionFile = 0;

    int fullMove = 3;
    int halfMove = 3;

    // assert
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(pawn, gamestate.bitboards.pawn, "pawn bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(rook, gamestate.bitboards.rook, "rook bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(knight, gamestate.bitboards.knight, "knight bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(bishop, gamestate.bitboards.bishop, "bishop bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(queen, gamestate.bitboards.queen, "queen bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(king, gamestate.bitboards.king, "king bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(occupancy, gamestate.bitboards.occupancy, "occupancy bitboard");

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(white, gamestate.bitboards.color[1], "white bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(black, gamestate.bitboards.color[0], "black bitboard");

    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.isWhiteTurn, "isWhiteTurn");
    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.kCastle[1], "white king castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.qCastle[1], "white queen castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.kCastle[0], "black king castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.qCastle[0], "black queen castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.canEnPassant, "can en passant");

    TEST_ASSERT_EQUAL_INT_MESSAGE(positionRank, gamestate.enPassantPosition.rank, "en passant rank");
    TEST_ASSERT_EQUAL_INT_MESSAGE(positionFile, gamestate.enPassantPosition.file, "en passant file");

    TEST_ASSERT_EQUAL_INT_MESSAGE(fullMove, gamestate.counters.fullMove, "full move counter");
    TEST_ASSERT_EQUAL_INT_MESSAGE(halfMove, gamestate.counters.halfMove, "half move counter");

}

void test_converter_fenToBitboard_workingInput3(void) {
    Gamestate gamestate = gamestateInit();
    char* fen = "6k1/3qbp2/4n1p1/3QP2p/7P/1B3NP1/5PK1/8 b - - 3 40"; 

    fenToBitboard(fen, &gamestate);

    // Bitboards
    uint64_t pawn =     0x0004020901020400; 
    uint64_t rook =     0x0000000000000000;
    uint64_t knight =   0x0000080000040000;
    uint64_t bishop =   0x0008000000400000;
    uint64_t queen =    0x0010001000000000;
    uint64_t king =     0x0200000000000200; 

    //color
    uint64_t white =    0x0000001801460600;
    uint64_t black =    0x021C0A0100000000;

    // occupancy
    uint64_t occupancy = pawn | rook | knight | bishop | queen | king;

    int positionRank = 0;
    int positionFile = 0;

    int fullMove = 40;
    int halfMove = 3;

    // assert
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(pawn, gamestate.bitboards.pawn, "pawn bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(rook, gamestate.bitboards.rook, "rook bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(knight, gamestate.bitboards.knight, "knight bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(bishop, gamestate.bitboards.bishop, "bishop bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(queen, gamestate.bitboards.queen, "queen bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(king, gamestate.bitboards.king, "king bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(occupancy, gamestate.bitboards.occupancy, "occupancy bitboard");

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(white, gamestate.bitboards.color[1], "white bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(black, gamestate.bitboards.color[0], "black bitboard");

    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.isWhiteTurn, "isWhiteTurn");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.kCastle[1], "white king castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.qCastle[1], "white queen castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.kCastle[0], "black king castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.qCastle[0], "black queen castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.canEnPassant, "can en passant");

    TEST_ASSERT_EQUAL_INT_MESSAGE(positionRank, gamestate.enPassantPosition.rank, "en passant rank");
    TEST_ASSERT_EQUAL_INT_MESSAGE(positionFile, gamestate.enPassantPosition.file, "en passant file");

    TEST_ASSERT_EQUAL_INT_MESSAGE(fullMove, gamestate.counters.fullMove, "full move counter");
    TEST_ASSERT_EQUAL_INT_MESSAGE(halfMove, gamestate.counters.halfMove, "half move counter");

}

void test_converter_fenToBitboard_workingInput4(void) {
    //setupJump();
    if (exit_status != 0) {
        return;
    }
    Gamestate gamestate = gamestateInit();
    char* fen = "r1b1k2r/2q1bppp/p1nppn2/Pp6/4PP2/1NNB4/1PP3PP/R1BQ1RK1 w kq b6 0 12";

    fenToBitboard(fen, &gamestate);

    // Bitboards
    uint64_t pawn =     0x000798C00C006300; 
    uint64_t rook =     0x8100000000000084;
    uint64_t knight =   0x0000240000600000;
    uint64_t bishop =   0x2008000000100020;
    uint64_t queen =    0x0020000000000010;
    uint64_t king =     0x0800000000000002; 

    //color
    uint64_t white =    0x000000800C7063B6;
    uint64_t black =    0xA92FBC4000000000;

    // occupancy
    uint64_t occupancy = pawn | rook | knight | bishop | queen | king;

    int positionRank = 6;
    int positionFile = 2;

    int fullMove = 12;
    int halfMove = 0;

    // assert
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(pawn, gamestate.bitboards.pawn, "pawn bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(rook, gamestate.bitboards.rook, "rook bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(knight, gamestate.bitboards.knight, "knight bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(bishop, gamestate.bitboards.bishop, "bishop bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(queen, gamestate.bitboards.queen, "queen bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(king, gamestate.bitboards.king, "king bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(occupancy, gamestate.bitboards.occupancy, "occupancy bitboard");

    TEST_ASSERT_EQUAL_UINT64_MESSAGE(white, gamestate.bitboards.color[1], "white bitboard");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(black, gamestate.bitboards.color[0], "black bitboard");

    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.isWhiteTurn, "isWhiteTurn");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.kCastle[1], "white king castle");
    TEST_ASSERT_FALSE_MESSAGE(gamestate.flags.qCastle[1], "white queen castle");
    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.kCastle[0], "black king castle");
    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.qCastle[0], "black queen castle");
    TEST_ASSERT_TRUE_MESSAGE(gamestate.flags.canEnPassant, "can en passant");

    TEST_ASSERT_EQUAL_INT_MESSAGE(positionRank, gamestate.enPassantPosition.rank, "en passant rank");
    TEST_ASSERT_EQUAL_INT_MESSAGE(positionFile, gamestate.enPassantPosition.file, "en passant file");

    TEST_ASSERT_EQUAL_INT_MESSAGE(fullMove, gamestate.counters.fullMove, "full move counter");
    TEST_ASSERT_EQUAL_INT_MESSAGE(halfMove, gamestate.counters.halfMove, "half move counter");

}

static void __loadTests() {
    RUN_TEST(test_converter_fenToBitboard_workingInput1); 
    RUN_TEST(test_converter_fenToBitboard_workingInput2);
    RUN_TEST(test_converter_fenToBitboard_workingInput3);
    RUN_TEST(test_converter_fenToBitboard_workingInput4);
}

void runTests_fenToBitboard() {
    UNITY_BEGIN();
    __loadTests();
    UNITY_END();
}

void includeTests_fenToBitboard() {
    UnitySetTestFile(__FILE__);
    __loadTests();
}
