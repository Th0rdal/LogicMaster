#include "structs/position.h"

/*
 * creates and initializes a Position struct. Initialization with 0
 *
 * @return: initialized Position struct
 *
 */
Position positionInit() {
    Position position;
    position.rank = 0;
    position.file = 0;
    return position;
}

/*
 * chceks if the values of Position struct is within the defined values 1-8
 *
 * @param position: the position to check
 *
 * @exit ERROR_POSITION_VALUE_OUTSIDE_EXPECTED: if a value is outside of the defined values
 *
 * */
void checkPositionValues(Position* position){
    //if both are 0, it is cleared
    if (position->rank == 0 && position->file == 0) {
        return;
    }
    if (position->rank > 8 || position-> rank < 1) {
        throwError(ERROR_POSITION_VALUE_OUTSIDE_EXPECTED, "Error in Position: rank value '%d' is outside the defined values 1-8", position->rank);
    }
    if (position->file > 8 || position->file < 1) {
        throwError(ERROR_POSITION_VALUE_OUTSIDE_EXPECTED, "Error in Position: file value '%d' is outside the defined values 1-8", position->file);
    }
}

/**
 * fills given position struct based on the number given in short. 0 = A8, 7 = A1
 *
 * @param position: pointer to position struct that should be written to
 * @param number: short number that represents the position
 *
 * */
void setPositionFromShort(Position* position, short number) {
    position->rank = number / 8 + 1;
    position->file = 8 - (number % 8);
    checkPositionValues(position);
}

/*
 * returns a Position array with all positions that are within the bitboard.
 * A position is set, if the bit at the position is 1
 * RESULT NEEDS TO BE FREED
 * No pieces should be filtered out before calling this function. Will return pointer to nothing.
 * Must still be freed
 *
 * @param bitboard: uint64_t bitboard with the positions
 * @param pieces: short with the amount of 1 in the bitboard. Can be calculated with __builtin_popcountll which counts all 1
 *
 * */
Position* getAllPiecePositions(uint64_t bitboard, short pieces) {
    if (pieces < 0 ) {
        throwError(ERROR_PIECE_POSITION_CANNOT_BE_CALCULATED, "Error the piece position given '%d' is negative", pieces);
    } else if (pieces > 63) {
        throwError(ERROR_PIECE_POSITION_CANNOT_BE_CALCULATED, "Error the piece position given '%d' is bigger than 63", pieces);
    }
    Position* piecePositions = (Position*) malloc(sizeof(Position)*pieces);

    uint64_t temp = 1;
    short j = 63;
    for (short i = 0; i < pieces; i++) {
        for (;j >= 0; j--) {
            if (bitboard & (temp << j)) {
                setPositionFromShort((piecePositions+i), j); 
                j--;
                break;
            }
        }
    }
    return piecePositions;
}

/*
 * convert a char Array to a position. This function expects a char array with length 2. All further characters will be ignored!
 *
 * @param fenChar: char array with the position to convert
 * @param position: Position struct to save the position in
 *
 * @exit ERROR_CANNOT_CONVERT_POSITION: either value cannot be converted to number between 1 and 8 or is not defined
 *
 */
void convertCharArrayToPosition(char* fenChar, Position* position) {
    short temp;
    char file = *fenChar;
    char rank = *(fenChar+1);

    if (isdigit(rank)) {
        temp = rank - '0';
        if (temp > 8 || temp < 1) {
            throwError(ERROR_POSITION_VALUE_OUTSIDE_EXPECTED, "Error in converting char position to Position struct: rank: character file '%c' does not convert to a number between 1 and 8.\n", rank);
        }
        position->rank = temp;
    } else {
        throwError(ERROR_POSITION_VALUE_OUTSIDE_EXPECTED, "Error in converting char position to Position struct: rank: character '%c' not defined.\n", rank);
    }

    if (isalpha(file)) {
        temp = file - '`';
        if (temp > 8 || temp < 0) {
            throwError(ERROR_POSITION_VALUE_OUTSIDE_EXPECTED, "Error in converting char position to Position struct: file: character rank '%c' does not convert to a number between 1 and 8.\n", file);
        }
        position->file = temp;
    } else {
        throwError(ERROR_POSITION_VALUE_OUTSIDE_EXPECTED, "Error in converting char position to Position struct: file: character '%c' not defined.\n", file);
    }
}

/*
 * converts the position into the index needed for the diagonal
 *
 * @param position: the position to convert
 *
 * @return: index of diagonal
 *
 * */
short getDiagonalIndexFromPosition(Position* position) {
    return 8 - position->rank + (8 - position->file);
}

short getDiagonalIndexFromShort(short position) {
    return 8 - (position/8+1) + (8 - position%8) - 1;
}

/*
 * converts the position into the index needed for the anti-diagonal
 *
 * @param position: the position to convert
 *
 * @return: index of anti-diagonal
 *
 * */short getAntiDiagonalIndexFromPosition(Position* position) {
    return 8 - position->rank + (8 - (8 - position->file));
}

short getAntiDiagonalIndexFromShort(short position) {
    return 8 - (position/8+1) + (8 - (8 - position%8));
}
