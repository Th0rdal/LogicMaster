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
    // end test cases
    printf("\x1b[0m");
    return UNITY_END();
}
