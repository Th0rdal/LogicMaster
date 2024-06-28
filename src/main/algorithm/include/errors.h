#ifndef ERRORCODES_H
#define ERRORCODES_H

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <ctype.h>

// memory 
#define ERROR_MEMORY_MALLOC_FAILED 10
#define ERROR_MEMORY_REALLOC_FAILED 11

// threads
#define ERROR_THREADS_CREATION_FAILED 15

// basic data structs
#define ERROR_CUSTOM_STACK_OVERFLOW 21
#define ERROR_CUSTOM_STACK_UNDERFLOW 22

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
#define WARNING_QUEUE_TOO_SMALL 502
#define WARNING_RESIZING_QUEUE 503
/*!
 * throws an error and then exists the program
 *
 * @param errorCode: the errorcode of the error
 * @param format: a char* with an error message. Variables can be given as %d, %s, etc...
 * @param ...: any variable "declared" in format
 *
 * */
void throwError(int errorCode, char* format, ...);

/*!
 * throws a warning to stderr and then returns
 *
 * @param warningCode: the warningCode of the warning
 * @param format: a char* with the error message. Variables can be given as %d, %s, etc...
 * @param ...: any variables "declared" in format
 *
 * */
void throwWarning(int warningCode, char* format, ...);

#endif

