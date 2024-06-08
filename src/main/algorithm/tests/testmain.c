#include "include/testmain.h"

#include "utility/utility.h"

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

    int expected1 = 123;
    int expected2 = 4;
    int expected3 = 59;

    int actual1 = convertCharArrayToInt(testValue1);
    int actual2 = convertCharArrayToInt(testValue2);
    int actual3 = convertCharArrayToInt(testValue3);

    printf("\x1b[31m");
    TEST_ASSERT_EQUAL_INT(expected1, actual1);
    TEST_ASSERT_EQUAL_INT(expected2, actual2);
    TEST_ASSERT_EQUAL_INT(expected3, actual3);

}

int main(void) {
    UNITY_BEGIN();

    // start test cases
    RUN_TEST(test_convertCharArrayToInt_perfect_input);
    // end test cases
    printf("\x1b[0m");
    return UNITY_END();
}
