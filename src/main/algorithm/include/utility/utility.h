#ifndef UTILITY_H
#define UTILITY_H

#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "errors.h"

/*!
 * converts a char array into an integer. Allows negative number
 *
 * @param charNumber: pointer to array to convert to number
 *
 * @return: integer representing the char array
 * @warning ERROR_CONVERTING_CHARARRAY_TO_INT: if there is an unexpected character in the array
 *
 */
int convertCharArrayToInt(char* charNumber);

/*
 * reverses a uint64_t bitwise
 *
 * @param: number to reverse
 *
 * @return: number bitwise reversed
 *
 */
uint64_t reverseBitboard(uint64_t bitboard);
#endif
