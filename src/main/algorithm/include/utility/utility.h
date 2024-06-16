#ifndef UTILITY_H
#define UTILITY_H

#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "errors.h"

int convertCharArrayToInt(char* charNumber);
uint64_t reverseBitboard(uint64_t bitboard);
#endif
