#include "utility/converter.h"

static void getFenPart(char* fen, short* startIndex, char* part);
static void setPieceOnBitboard(char fenChar, uint64_t value, Bitboards* bitboards);
static void convertCharArrayToPosition(char* fenChar, Position* position);

/**
 * Converts a fen notation char array into a bitboard struct and writes it into the bitboard struct
 * Expects en Passant position in format a-h1-8 e.g., a5, e3
 *
 * @param fen: fen notation char array
 * @param bitboards: Bitboards struct pointer to empty bitboard
 *
 * */
void fenToBitboard(char* fen, Gamestate* gamestate) {
    short maxLoopCounter = 0;
    short counter = 0;
    short partCounter = 0;
    short pieceCounter = 0;
    char fenChar = fen[0];
    char part[128];

    while (fen[counter] != '\0' && maxLoopCounter < 256) {
        maxLoopCounter++;
        fenChar = fen[counter];
        
        if (isspace(fenChar)) {
            counter++;
            partCounter++;
            continue;
        }
        
        switch (partCounter) {
            case 1:
                gamestate->flags.isWhiteTurn = fenChar == 'w';
                counter++;
                continue;
            case 2:
                // no need to filter for - (no castling anymore), because it switches away
                // from case 2 after the first counter. 
                switch (fenChar) {
                    case 'K':
                        gamestate->flags.whiteKCastle = true;
                        break;
                    case 'Q':
                        gamestate->flags.whiteQCastle = true;
                        break;
                    case 'k':
                        gamestate->flags.blackKCastle = true;
                        break;
                    case 'q':
                        gamestate->flags.blackQCastle = true;
                        break;
                }
                counter++;
                continue;
            case 3:
                if (fenChar == '-') {
                    counter++;
                    continue;
                }
                gamestate->flags.canEnPassant = true;
                getFenPart(fen, &counter, part);
                part[0] = 'k';
                convertCharArrayToPosition(part, &gamestate->enPassantPosition);
                continue;
            case 4:
                getFenPart(fen, &counter, part);
                gamestate->counters.halfMove = atoi(part);
                continue;
            case 5:
                getFenPart(fen, &counter, part);
                gamestate->counters.fullMove = atoi(part);
                continue;
        }
       
        if (islower(fenChar)) { // lower case piece means black piece
            uint64_t temp = pow(2, 63 - pieceCounter);
            gamestate->bitboards.black += temp;
            gamestate->bitboards.occupancy += temp;
            setPieceOnBitboard(fenChar, temp, &gamestate->bitboards);
            pieceCounter++;
        } else if (isupper(fenChar)) { // upper case piece means white piece
            uint64_t temp = pow(2, 63 - pieceCounter);
            gamestate->bitboards.white += temp;
            gamestate->bitboards.occupancy += temp;
            setPieceOnBitboard(fenChar, temp, &gamestate->bitboards);
            pieceCounter++;
        } else if (isdigit(fenChar)) { // digit means that many spaces are empty
            pieceCounter += fenChar - '0';
        } else if (isspace(fenChar)) { // if space, next part
            partCounter++;
        } else if (fenChar == '/') { // used to say next line. not needed here
            counter++;
            continue;
        }
        counter++;
    }
    if (maxLoopCounter >= 127) {
        printf("ERROR"); //TODO make throwWarning with new max loop count reached
    }

}

/**
 * adds the value to the bitboard given by the fenChar
 *
 * @param fenChar: character representation of the piece
 * @param value: the uint64_t value representing the square the piece is on
 * @param bitboards: Bitboard struct pointer representing the board
 *
 */
static void setPieceOnBitboard(char fenChar, uint64_t value, Bitboards* bitboards) {
    switch (fenChar) {
        case 'k':
        case 'K':
            bitboards->king += value;
            break;
        case 'q':
        case 'Q':
            bitboards->queen += value;
            break;
        case 'n':
        case 'N':
            bitboards->knight += value;
            break;
        case 'r':
        case 'R':
            bitboards->rook += value;
            break;
        case 'p':
        case 'P':
            bitboards->pawn += value;
            break;
        case 'b':
        case 'B':
            bitboards->bishop += value;
            break;
        default:
            throwError(ERROR_FEN_CHAR_NOT_DEFINED, "Error in converting fen notation to bitboards: character '%c' not defined.\n", fenChar);   
    }
}

/**
 * fetches the next part of the fen notation. This is marked by a space
 *
 * @param fen: the complete fen notation char pointer
 * @param startIndex: pointer of counter, representing the first index of the next fen notation part. THIS MUST NOT BE SPACE!
 * @param part: char array to save the result in
 *
 * @exit ERROR_FETCHING_FEN_PART: If after completing fetching, the next character is not a space
 *
 */
static void getFenPart(char* fen, short* startIndex, char* toWrite) {
    memset(toWrite, '\0', strlen(toWrite)+1); // reset toWrite string
    int i;

    for (i = 0; i < 4 && !isspace(fen[*startIndex+i]) && fen[*startIndex+i] != '\0'; i++) {
        toWrite[i] = fen[*startIndex+i];
    }
    if (!isspace(fen[*startIndex+i]) && fen[*startIndex+i] != '\0') { // sanity check if the next character is space. Can happen if part is longer than 128 character
        throwError(ERROR_FETCHING_FEN_PART, "Error in trying to fetch next fen notation part. Next character after fetching '%s' from '%s' is not a space\n", toWrite, fen);
    }
    *startIndex += i; // add i to the current counter.
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
static void convertCharArrayToPosition(char* fenChar, Position* position) {
    short temp;
    char file = *fenChar;
    char rank = *(fenChar+1);

    if (isdigit(rank)) {
        temp = rank - '0';
        if (temp > 8 || temp < 1) {
            throwError(ERROR_CANNOT_CONVERT_POSITION, "Error in converting char position to Position struct: rank: character file '%c' does not convert to a number between 1 and 8.\n", rank);
        }
        position->rank = temp;
    } else {
        throwError(ERROR_CANNOT_CONVERT_POSITION, "Error in converting char position to Position struct: rank: character '%c' not defined.\n", rank);
    }

    if (isalpha(file)) {
        temp = file - '`';
        if (temp > 8 || temp < 0) {
            throwError(ERROR_CANNOT_CONVERT_POSITION, "Error in converting char position to Position struct: file: character rank '%c' does not convert to a number between 1 and 8.\n", file);
        }
        position->file = temp;
    } else {
        throwError(ERROR_CANNOT_CONVERT_POSITION, "Error in converting char position to Position struct: file: character '%c' not defined.\n", file);
    }
}


