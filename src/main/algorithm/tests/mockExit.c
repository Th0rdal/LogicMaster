#define _CRT_SECURE_NO_WARNINGS

#include "include/mockExit.h"

jmp_buf exit_jump;
int exit_status;

void mockExit(int status) {
    exit_status = status;
    longjmp(exit_jump, 1);
    __builtin_unreachable();
}

_Noreturn void throwError(int errorCode, char* format __attribute__((unused)), ...) {
    mockExit(errorCode);
    __builtin_unreachable();
}
