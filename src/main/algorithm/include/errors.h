#ifndef ERRORCODES_H
#define ERRORCODES_H

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <ctype.h>

// memory 
#define ERROR_MEMORY_MALLOC_FAILED 10
#define ERROR_MEMORY_REALLOC_FAILED 11

// no input to program
#define ERROR_NOINPUT 40

//fen convertion errors
#define ERROR_FEN_CHAR_NOT_DEFINED 100
#define ERROR_FEN_NUMBER_CONVERTION_CHAR_TOO_LARGE 102 
#define ERROR_FETCHING_FEN_PART 103

// utility errors
#define ERROR_CONVERTING_CHARARRAY_TO_INT 104

// position errors
#define ERROR_POSITION_VALUE_OUTSIDE_EXPECTED 110
#define ERROR_PIECE_POSITION_CANNOT_BE_CALCULATED 111

// move generation
#define ERROR_PIECE_NOT_SELECTED 120


#define WARNING_COULD_NOT_RETRIEVE_CORE_NUMBER 501 
extern void throwError(int errorCode, char* format, ...);
extern void throwWarning(int warningCode, char* format, ...);

#endif

