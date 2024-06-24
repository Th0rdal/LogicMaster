#include "structs/position.h"

Position positionInit() {
    Position position;
    position.rank = 0;
    position.file = 0;
    return position;
}

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


void setPositionFromShort(Position* position, const short number) {
    position->rank = number / 8 + 1;
    position->file = 8 - (number % 8);
    checkPositionValues(position);
}


Position* getAllPiecePositions(const uint64_t bitboard, const short pieces) {
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


void convertCharArrayToPosition(const char* fenChar, Position* position) {
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

short getDiagonalIndexFromPosition(const Position* position) {
    return 8 - position->rank + (8 - position->file);
}

short getDiagonalIndexFromShort(const short position) {
    return 8 - (position/8+1) + (8 - position%8) - 1;
}

short getAntiDiagonalIndexFromPosition(const Position* position) {
    return 8 - position->rank + (8 - (8 - position->file));
}


short getAntiDiagonalIndexFromShort(const short position) {
    return 8 - (position/8+1) + (8 - (8 - position%8));
}


short positionToShort(const Position* position) {
    return (position->rank - 1) * 8 + (8 - position->file); 
}


void deepCopyPosition(Position* destination, const Position* source)  {
    destination->rank = source->rank;
    destination->file = source->file;
}
