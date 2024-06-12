#include "include/testmain.h"


void setUp() {
    setupThrow(); //sets shouldThrow false and exit_status 0
}

void tearDown() {
}


int main(void) {
    printf("Beginning testing...\n");
    UNITY_BEGIN();

    // start test cases
    includeTests_convertCharArrayToInt();
    includeTests_fenToBitboard();
    // end test cases
    printf("\x1b[0m");
    return UNITY_END();
}
