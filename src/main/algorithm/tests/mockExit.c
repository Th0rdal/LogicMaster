#define _CRT_SECURE_NO_WARNINGS

#include "include/mockExit.h"

jmp_buf exit_jump;
int exit_status;
bool shouldThrow = false; // true when a ERROR in the code is expected. if true, it is expected that the error is handled within the test case
char errorMessage[1024];
va_list args;

void setupThrow() {
    shouldThrow = false;
    exit_status = 0;
}

_Noreturn void throwError(int errorCode, char* format, ...) {
    
    exit_status = errorCode;

    if (shouldThrow) {
        longjmp(exit_jump, 1);
        __builtin_unreachable();
    }

    // for error messages if the error was not expected
    strcat(errorMessage, "\n\tUnexpected throw (line number not correct):\n\t\t");
    char temp[512];
    va_start(args, format);
    vsprintf(temp, format, args);
    va_end(args);
    strcat(errorMessage, temp);

    TEST_FAIL_MESSAGE(errorMessage);
    __builtin_unreachable();
}
