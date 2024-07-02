#include "tests\test_reverseBitboard.h"

void test_utility_reverseBitboard() {
    uint64_t value1 = 0x0020408094002000; 
    uint64_t value2 = 0x0000000000000000;
    uint64_t value3 = 0x0000080000040000;
    uint64_t value4 = 0x0008000000400000;
    uint64_t value5 = 0x0010001000000000;
    uint64_t value6 = 0x0200000000000200;
    uint64_t value7 = 0x0000001801460600;
    uint64_t value8 = 0x021C0A0100000000;
    uint64_t value9 = 0x021c0a1901460600;

    uint64_t expected1 = 0x0004002901020400; 
    uint64_t expected2 = 0x0000000000000000;
    uint64_t expected3 = 0x0000200000100000; 
    uint64_t expected4 = 0x0000020000001000;
    uint64_t expected5 = 0x0000000008000800;
    uint64_t expected6 = 0x0040000000000040;
    uint64_t expected7 = 0x0060628018000000;
    uint64_t expected8 = 0x0000000080503840;
    uint64_t expected9 = 0x0060628098503840;

    uint64_t actual1 = reverseBitboard(value1);
    uint64_t actual2 = reverseBitboard(value2);
    uint64_t actual3 = reverseBitboard(value3);
    uint64_t actual4 = reverseBitboard(value4);
    uint64_t actual5 = reverseBitboard(value5);
    uint64_t actual6 = reverseBitboard(value6);
    uint64_t actual7 = reverseBitboard(value7);
    uint64_t actual8 = reverseBitboard(value8);
    uint64_t actual9 = reverseBitboard(value9);
    
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected1, actual1, "1");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected2, actual2, "2");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected3, actual3, "3");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected4, actual4, "4");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected5, actual5, "5");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected6, actual6, "6");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected7, actual7, "7");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected8, actual8, "8");
    TEST_ASSERT_EQUAL_UINT64_MESSAGE(expected9, actual9, "9");

}

static void __loadTests() {
    RUN_TEST(test_utility_reverseBitboard);
}

void runTests_reverseBitboard() {
    UNITY_BEGIN();
    __loadTests();
    UNITY_END();
}

void includeTests_reverseBitboard() {
    UnitySetTestFile(__FILE__);
    __loadTests();
}









