#ifndef ERRORCODES_H
#define ERRORCODES_H

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#define ERROR_FEN_CHAR_NOT_DEFINED 100
#define ERROR_CANNOT_CONVERT_POSITION 101
#define ERROR_FEN_NUMBER_CONVERTION_CHAR_TOO_LARGE 102 
#define ERROR_FETCHING_FEN_PART 103
#define ERROR_CONVERTING_CHARARRAY_TO_INT 104

extern void throwError(int errorCode, char* format, ...);
extern void throwWarning(int warningCode, char* format, ...);

#endif

