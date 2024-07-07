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
            specification = "Memory allocation failed";
            break;
        case ERROR_MEMORY_REALLOC_FAILED:
            specification = "Memory reallocation failed";
            break;
        case ERROR_THREADS_CREATION_FAILED:
            specification = "Thread creation failed";
            break;
        case ERROR_THREADS_TIMEOUT:
            specification = "Thread timeout";
            break;
        case ERROR_CUSTOM_STACK_OVERFLOW:
            specification = "Stack overflow";
            break;
        case ERROR_CUSTOM_STACK_UNDERFLOW:
            specification = "Stack underflow";
            break;
        case ERROR_QUEUE_TOO_SMALL:
            specification = "Queue too small";
            break;
        case ERROR_NOINPUT:
            specification = "No input given";
            break;
        case ERROR_UNKNOWN_ARGUMENT:
            specification = "Unknown program argument";
            break;
        case ERROR_FEN_CHAR_NOT_DEFINED:
            specification = "Fen char not defined";
            break;
        case ERROR_FEN_NUMBER_CONVERTION_CHAR_TOO_LARGE:
            specification = "Fen number convertion char is too large";
            break;
        case ERROR_FETCHING_FEN_PART:
            specification = "Fen fetching part";
            break;
        case ERROR_CONVERTING_CHARARRAY_TO_INT:
            specification = "Fen converting chararray to int";
            break;
        case ERROR_POSITION_VALUE_OUTSIDE_EXPECTED:
            specification = "Position value outside defined range";
            break;
        case ERROR_PIECE_POSITION_CANNOT_BE_CALCULATED:
            specification = "Piece position cannot be calculated";
            break;
        case ERROR_PROMOTION_PIECE_UNAVAILABLE:
            specification = "Promotion piece unavailable";
            break;
        case ERROR_PIECE_NOT_SELECTED:
            specification = "No piece selected";
            break;
        default:
            specification = "No specification defined";
    }
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
    printf("\x1b[33m" "Warning code: %d\n", warningCode);
    vfprintf(stderr, format, args);
    fprintf(stderr, "\n");
    printf("\x1b[0m");
    va_end(args);
}
