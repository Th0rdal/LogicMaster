#include "tests/test_convertCharArrayToInt.h"

void test_convertCharArrayToInt_perfect_input(void) {
    char* testValue1 = "123";
    char* testValue2 = "4";
    char* testValue3 = "59";
    char* testValue4 = "-687";

    int expected1 = 123;
    int expected2 = 4;
    int expected3 = 59;
    int expected4 = -687;

    int actual1 = convertCharArrayToInt(testValue1);
    int actual2 = convertCharArrayToInt(testValue2);
    int actual3 = convertCharArrayToInt(testValue3);
    int actual4 = convertCharArrayToInt(testValue4);

    TEST_ASSERT_EQUAL_INT(expected1, actual1);
    TEST_ASSERT_EQUAL_INT(expected2, actual2);
    TEST_ASSERT_EQUAL_INT(expected3, actual3);
    TEST_ASSERT_EQUAL_INT(expected4, actual4);
}

void test_convertCharArrayToInt_nonDigit_input(void) {
    shouldThrow = true;

    char* testValue1 = "d";
    char* testValue2 = "3IAN";
    char* testValue3 = "-3f";

    TEST_ASSERT_EQUAL_THROW(ERROR_CONVERTING_CHARARRAY_TO_INT);
    convertCharArrayToInt(testValue1);
    THROW_CLEANUP();

    TEST_ASSERT_EQUAL_THROW_MESSAGE(ERROR_CONVERTING_CHARARRAY_TO_INT, "Illegal character 'I' after number does not make it fail");
    convertCharArrayToInt(testValue2);
    THROW_CLEANUP();
    
    TEST_ASSERT_EQUAL_THROW_MESSAGE(ERROR_CONVERTING_CHARARRAY_TO_INT, "Illegal character 'f' after number and minus sign does not make it fail");
    convertCharArrayToInt(testValue3);
    THROW_CLEANUP();
        
    
}

static void __loadTests() {
    RUN_TEST(test_convertCharArrayToInt_perfect_input);
    RUN_TEST(test_convertCharArrayToInt_nonDigit_input);
}

void runTests_convertCharArrayToInt() {
    UNITY_BEGIN();
    __loadTests();
    UNITY_END();
}

void includeTests_convertCharArrayToInt() {
    UnitySetTestFile(__FILE__);
    __loadTests();
}
