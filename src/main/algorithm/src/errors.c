#include "errors.h"

_Noreturn void throwError(int exitCode, char* format, ...) {
    va_list args;
    va_start(args, format);
    printf("\x1b[31m");
    vfprintf(stderr, format, args);
    printf("\x1b[0m");
    va_end(args);
    exit(exitCode);
}

void throwWarning(int warningCode, char* format, ...) {
    va_list args;
    va_start(args, format);
    printf("\x1b[33" "Warning code: %d", warningCode);
    vfprintf(stderr, format, args);
}
