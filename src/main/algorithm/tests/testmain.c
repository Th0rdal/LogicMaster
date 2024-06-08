#include "include/testmain.h"


void setUp() {
    printf("\x1b[0m");
}

void tearDown() {
    printf("\x1b[32m");
}

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

    printf("\x1b[31m");
    TEST_ASSERT_EQUAL_INT(expected1, actual1);
    TEST_ASSERT_EQUAL_INT(expected2, actual2);
    TEST_ASSERT_EQUAL_INT(expected3, actual3);
    TEST_ASSERT_EQUAL_INT(expected4, actual4);
}

void test_convertCharArrayToInt_nonDigit_input(void) {
    char* testValue1 = "d";
    char* testValue2 = "3IAN";
    char* testValue3 = "-3f";

    printf("\x1b[31m");

    exit_status = 0;
    if (setjmp(exit_jump) == 0) {
        convertCharArrayToInt(testValue1);
    }
    TEST_ASSERT_EQUAL_MESSAGE(ERROR_CONVERTING_CHARARRAY_TO_INT, exit_status, "Single character 'd' does not make it fail");

    exit_status = 0;
    if (setjmp(exit_jump) == 0) {
        convertCharArrayToInt(testValue2);
    }
    TEST_ASSERT_EQUAL_MESSAGE(ERROR_CONVERTING_CHARARRAY_TO_INT, exit_status, "Illegal character 'I' after number does not make it fail");
    
    exit_status = 0;
    if (setjmp(exit_jump) == 0) {
        convertCharArrayToInt(testValue3);
    }
    TEST_ASSERT_EQUAL_MESSAGE(ERROR_CONVERTING_CHARARRAY_TO_INT, exit_status, "Illegal character 'f' after number and minus sign does not make it fail");
        
    
}

int main(void) {
    printf("Beginning testing...\n");
    UNITY_BEGIN();

    // start test cases
    
    RUN_TEST(test_convertCharArrayToInt_perfect_input);
    RUN_TEST(test_convertCharArrayToInt_nonDigit_input);

    // end test cases
    printf("\x1b[0m");
    return UNITY_END();
}
