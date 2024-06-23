#define _CRT_SECURE_NO_WARNINGS

#include "include/mockExit.h"

jmp_buf exit_jump;
int exit_status;
bool shouldThrow = false; // true when a ERROR in the code is expected. if true, it is expected that the error is handled within the test case
static char errorMessage[1024];
static va_list args;

_Noreturn void throwError(int errorCode, char* format, ...) {
    
    exit_status = errorCode;

    if (shouldThrow) {
        longjmp(exit_jump, 1);
        __builtin_unreachable();
    } else {

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
    __builtin_unreachable();
}

void throwWarning(int warningCode, char* format, ...) {
    va_list args;
    va_start(args, format);
    printf("\x1b[33" "Warning code: %d", warningCode);
    vfprintf(stderr, format, args);
}
