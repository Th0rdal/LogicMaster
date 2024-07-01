#include "include/testmain.h"


void setUp() {
    shouldThrow = false;
}

void tearDown() {
}


int main(void) {
    printf("Beginning testing...\n");
    UNITY_BEGIN();

    // start test cases
    includeTests_convertCharArrayToInt();
    includeTests_fenToBitboard();
    includeTests_positionStruct();
    includeTests_reverseBitboard();
    includeTests_moveCalculator();
    includeTests_preprocessing();
    includeTests_makeMove();
    includeTests_evaluation();
    // end test cases
    printf("\x1b[0m");
    return UNITY_END();
}
