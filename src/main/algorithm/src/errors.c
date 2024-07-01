#include "errors.h"

/*!
 * prints an error code specific message for the error message
 *
 * @param exitCode: the errorcode of the error
 *
 * */
static inline void printErrorCodeSpecification(int exitCode) {
    char* specification;
    switch (exitCode) {
        case ERROR_MEMORY_MALLOC_FAILED:
            specification = "memory allocation failed";
            break;
        case ERROR_MEMORY_REALLOC_FAILED:
            specification = "memory reallocation failed";
            break;
        case ERROR_THREADS_CREATION_FAILED:
            specification = "thread creation failed";
            break;
        case ERROR_CUSTOM_STACK_OVERFLOW:
            specification = "stack overflow";
            break;
        case ERROR_CUSTOM_STACK_UNDERFLOW:
            specification = "stack underflow";
            break;
        case ERROR_NOINPUT:
            specification = "no input given";
            break;
        case ERROR_FEN_CHAR_NOT_DEFINED:
            specification = "fen char not defined";
            break;
        case ERROR_FEN_NUMBER_CONVERTION_CHAR_TOO_LARGE:
            specification = "fen number convertion char is too large";
            break;
        case ERROR_FETCHING_FEN_PART:
            specification = "fen fetching part";
            break;
        case ERROR_CONVERTING_CHARARRAY_TO_INT:
            specification = "fen converting chararray to int";
            break;
        case ERROR_POSITION_VALUE_OUTSIDE_EXPECTED:
            specification = "position value outside defined range";
            break;
        case ERROR_PIECE_POSITION_CANNOT_BE_CALCULATED:
            specification = "piece position cannot be calculated";
            break;
        case ERROR_PIECE_NOT_SELECTED:
            specification = "no piece selected";
            break;
        default:
            specification = "no specification defined";
    }
    specification[0] = toupper(specification[0]);

    fprintf(stderr, "%s error:\n\t", specification);
}

_Noreturn void throwError(int exitCode, char* format, ...) {
    va_list args;
    va_start(args, format);
    printf("\x1b[31m");
    printErrorCodeSpecification(exitCode);
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
