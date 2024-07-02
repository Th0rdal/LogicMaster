#include "tests/test_positionStruct.h"

void test_positionStruct_init() {
    Position position = positionInit();

    TEST_ASSERT_EQUAL_INT_MESSAGE(position.rank, 0, "Position rank does not initialize correctly");
    TEST_ASSERT_EQUAL_INT_MESSAGE(position.file, 0, "Position file does not initialize correctly");
}

void test_positionStruct_setPositionFromShort_perfectInput() {
    Position position1 = positionInit();
    Position position2 = positionInit();
    Position position3 = positionInit();
    Position position4 = positionInit();
    Position position5 = positionInit();

    setPositionFromShort(&position1, 0);
    setPositionFromShort(&position2, 63);
    setPositionFromShort(&position3, 44);
    setPositionFromShort(&position4, 26);
    setPositionFromShort(&position5, 31);

    TEST_ASSERT_EQUAL_INT(position1.rank, 1);
    TEST_ASSERT_EQUAL_INT(position1.file, 8);

    TEST_ASSERT_EQUAL_INT(position2.rank, 8);
    TEST_ASSERT_EQUAL_INT(position2.file, 1);

    TEST_ASSERT_EQUAL_INT(position3.rank, 6);
    TEST_ASSERT_EQUAL_INT(position3.file, 4);

    TEST_ASSERT_EQUAL_INT(position4.rank, 4);
    TEST_ASSERT_EQUAL_INT(position4.file, 6);

    TEST_ASSERT_EQUAL_INT(position5.rank, 4);
    TEST_ASSERT_EQUAL_INT(position5.file, 1);
}

void test_positionStruct_setPositionFromShort_wrongInput() {
    shouldThrow = true;
    
    Position position1 = positionInit();
    Position position2 = positionInit();
    Position position3 = positionInit();

    TEST_ASSERT_EQUAL_THROW(ERROR_POSITION_VALUE_OUTSIDE_EXPECTED);
    setPositionFromShort(&position1, 65); // correct line wrong file     
    THROW_CLEANUP();
    
    TEST_ASSERT_EQUAL_THROW(ERROR_POSITION_VALUE_OUTSIDE_EXPECTED);
    setPositionFromShort(&position2, 90); // wrong line and file
    THROW_CLEANUP();

    TEST_ASSERT_EQUAL_THROW(ERROR_POSITION_VALUE_OUTSIDE_EXPECTED);
    setPositionFromShort(&position3, -2); //wrong line and file
    THROW_CLEANUP();
}

void test_positionStruct_getAllPiecePositions_perfectInput() {
    uint64_t testBitboard[] = { 0x0000000000000081,
                                0x8000000000000000,
                                0x0000000010100000,
                                0x0010010000040000,
                                0x0000000000F00000};

    short expected1[2][2] = {
        {1, 1},
        {8, 1}
    };
    short expected2[1][2] = {{1, 8}};
    short expected3[2][2] = {
        {4, 4},
        {4, 3}
    };
    short expected4[3][2] = {
        {4, 7},
        {8, 6},
        {6, 3}
    };
    short expected5[4][2] = {
        {1, 3},
        {2, 3},
        {3, 3},
        {4, 3}
    };
    
    char* errorFile = "Error in file in %d Position";
    char* errorRank = "Error in rank in %d Position";

    short popCount = __builtin_popcountll(testBitboard[0]);
    Position* returnPositions = getAllPiecePositions(testBitboard[0], popCount);
    for (int i = 0; i < popCount; i++) {
        char msg1[128];
        char msg2[128];
        sprintf(msg1, errorFile , i);
        sprintf(msg2, errorRank, i);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected1[i][0], (returnPositions+i)->file, msg1);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected1[i][1], (returnPositions+i)->rank, msg2);
    }
    free(returnPositions);

    popCount = __builtin_popcountll(testBitboard[1]);
    returnPositions = getAllPiecePositions(testBitboard[1], popCount);
    for (int i = 0; i < popCount; i++) {
        char msg1[128];
        char msg2[128];
        sprintf(msg1, errorFile , i);
        sprintf(msg2, errorRank, i);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected2[i][0], (returnPositions+i)->file, msg1);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected2[i][1], (returnPositions+i)->rank, msg2);
    }
    free(returnPositions);

    popCount = __builtin_popcountll(testBitboard[2]);
    returnPositions = getAllPiecePositions(testBitboard[2], popCount);
    for (int i = 0; i < popCount; i++) {
        char msg1[128];
        char msg2[128];
        sprintf(msg1, errorFile , i);
        sprintf(msg2, errorRank, i);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected3[i][0], (returnPositions+i)->file, msg1);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected3[i][1], (returnPositions+i)->rank, msg2);
    }
    free(returnPositions);

    popCount = __builtin_popcountll(testBitboard[3]);
    returnPositions = getAllPiecePositions(testBitboard[3], popCount);
    for (int i = 0; i < popCount; i++) {
        char msg1[128];
        char msg2[128];
        sprintf(msg1, errorFile , i);
        sprintf(msg2, errorRank, i);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected4[i][0], (returnPositions+i)->file, msg1);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected4[i][1], (returnPositions+i)->rank, msg2);
    }
    free(returnPositions);

    popCount = __builtin_popcountll(testBitboard[4]);
    returnPositions = getAllPiecePositions(testBitboard[4], popCount);
    for (int i = 0; i < popCount; i++) {
        char msg1[128];
        char msg2[128];
        sprintf(msg1, errorFile , i);
        sprintf(msg2, errorRank, i);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected5[i][0], (returnPositions+i)->file, msg1);
        TEST_ASSERT_EQUAL_INT_MESSAGE(expected5[i][1], (returnPositions+i)->rank, msg2);
    }
    free(returnPositions);
}

void test_positionStruct_getAllPiecePositions_negativeInput() {
    shouldThrow = true;

    uint64_t bitboard = 0x1200000000000011; 

    TEST_ASSERT_EQUAL_THROW(ERROR_PIECE_POSITION_CANNOT_BE_CALCULATED);
    getAllPiecePositions(bitboard, -3);
    THROW_CLEANUP_MESSAGE("Should throw error, because of negative number as amount of pieces");
    
    TEST_ASSERT_EQUAL_THROW(ERROR_PIECE_POSITION_CANNOT_BE_CALCULATED);
    getAllPiecePositions(bitboard, -1);
    THROW_CLEANUP_MESSAGE("Should throw error, because of negative number as amount of pieces");

}

void test_positionStruct_getAllPiecePositions_toBigInput() {
    shouldThrow = true;

    uint64_t bitboard = 0x0000000000000000;

    TEST_ASSERT_EQUAL_THROW(ERROR_PIECE_POSITION_CANNOT_BE_CALCULATED);
    getAllPiecePositions(bitboard, 65);
    THROW_CLEANUP_MESSAGE("Should throw error, because more pieces given than spaces");

    TEST_ASSERT_EQUAL_THROW(ERROR_PIECE_POSITION_CANNOT_BE_CALCULATED);
    getAllPiecePositions(bitboard, 100);
    THROW_CLEANUP_MESSAGE("Should throw error, because more pieces given than spaces");
}

static void __loadTests() {
    RUN_TEST(test_positionStruct_init);
    RUN_TEST(test_positionStruct_setPositionFromShort_perfectInput);
    RUN_TEST(test_positionStruct_setPositionFromShort_wrongInput);
    RUN_TEST(test_positionStruct_getAllPiecePositions_perfectInput);
    RUN_TEST(test_positionStruct_getAllPiecePositions_negativeInput);
    RUN_TEST(test_positionStruct_getAllPiecePositions_toBigInput);
}

void runTests_positionStruct() {
    UNITY_BEGIN();
    __loadTests();
    UNITY_END();
}

void includeTests_positionStruct() {
    UnitySetTestFile(__FILE__);
    __loadTests();
}
