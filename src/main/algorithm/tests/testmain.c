#include "include/testmain.h"


void setUp() {
    printf("\x1b[0m");
}

void tearDown() {
    printf("\x1b[32m");
}


int main(void) {
    printf("Beginning testing...\n");
    UNITY_BEGIN();

    // start test cases
    
    
    convertCharArrayToInt_includeTests();

    // end test cases
    printf("\x1b[0m");
    return UNITY_END();
}
