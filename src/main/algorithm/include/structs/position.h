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

Position positionInit();
void checkPositionValues(Position* position);
void setPositionFromShort(Position* position, short number);
Position* getAllPiecePositions(uint64_t bitboard, short pieces);
void convertCharArrayToPosition(char* fenChar, Position* position);
short getDiagonalIndexFromPosition(Position* position);
short getDiagonalIndexFromShort(short position);
short getAntiDiagonalIndexFromPosition(Position* position);
short getAntiDiagonalIndexFromShort(short position);
short positionToShort(Position* position);
void deepCopyPosition(Position* destination, Position* source);
#endif
