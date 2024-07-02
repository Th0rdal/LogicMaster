#ifndef POSITION_H
#define POSITION_H

#include <stdint.h>
#include <stdlib.h>
#include <string.h>

#include "errors.h"

typedef struct { // initialized to 0
    short rank; // row
    short file; //column
} Position;

/*!
 * creates and initializes a Position struct. Initialization with 0
 *
 * @return: initialized Position struct
 *
 */
Position positionInit();

/*!
 * chceks if the values of Position struct is within the defined values 1-8
 *
 * @param position: the position to check
 *
 * @warning ERROR_POSITION_VALUE_OUTSIDE_EXPECTED: if a value is outside of the defined values
 *
 * */
void checkPositionValues(Position* position);

/*!
 * fills given position struct based on the number given in short. 0 = A8, 7 = A1
 *
 * @param position: pointer to position struct that should be written to
 * @param number: short number that represents the position
 *
 * */
void setPositionFromShort(Position* position, const short number);

/*!
 * returns a Position array with all positions that are within the bitboard.
 * A position is set, if the bit at the position is 1
 * RESULT NEEDS TO BE FREED
 * No pieces should be filtered out before calling this function. Will return pointer to nothing.
 * Must still be freed
 *
 * @param bitboard: uint64_t bitboard with the positions
 * @param pieces: short with the amount of 1 in the bitboard. Can be calculated with __builtin_popcountll which counts all 1
 *
 * @warning ERROR_POSITION_VALUE_OUTSIDE_EXPECTED: if the position value is not between 1 <= x <= 8
 *
 * */
Position* getAllPiecePositions(const uint64_t bitboard, const short pieces);

/*!
 * convert a char Array to a position. This function expects a char array with length 2. All further characters will be ignored!
 *
 * @param fenChar: char array with the position to convert
 * @param position: Position struct to save the position in
 *
 * @warning ERROR_POSITION_VALUE_OUTSIDE_EXPECTED: either value cannot be converted to number between 1 and 8 or is not defined
 *
 */
void convertCharArrayToPosition(const char* fenChar, Position* position);

/*!
 * converts the position into the index needed for the diagonal
 *
 * @param position: the position to convert
 *
 * @return: index of diagonal
 *
 * */
short getDiagonalIndexFromPosition(const Position* position);

/*!
 * converts the position into the index needed for the diagonal
 *
 * @param position: the position to convert
 *
 * @return: index of diagonal
 *
 * */
short getDiagonalIndexFromShort(const short position);

/*!
 * converts the position into the index needed for the anti-diagonal
 *
 * @param position: the position to convert
 *
 * @return: index of anti-diagonal
 *
 * */
short getAntiDiagonalIndexFromPosition(const Position* position);

/*!
 * converts the position into the index needed for the anti-diagonal
 *
 * @param position: the position to convert
 *
 * @return: index of anti-diagonal
 *
 * */
short getAntiDiagonalIndexFromShort(const short position);

/*!
 * converts a position to a short.
 *
 * @param position: the position to convert
 *
 * @return: index of the square represented by the position
 *
 * */
short positionToShort(const Position* position);

#endif
