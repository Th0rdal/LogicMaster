/**
 * Converts a fen notation char array into a bitboard struct and writes it into the bitboard struct
 * Expects en Passant position in format a-h1-8 e.g., a5, e3
 *
 * @param fen: fen notation char array
 * @param bitboards: Bitboards struct pointer to empty bitboard
 *
 * */
void fenToBitboard(char* fen, Gamestate* gamestate) {
    short counter = 0;
    short partCounter = 0;
    char fenChar = fen[0];
    Bitboards* bitboards = gamestate->bitboards;

    while (fenChar != '\0') {
        fenChar = fen[counter];
        
        if (isspace(fenChar)) {
            partCounter++;
            continue;
        }
        
        switch (counter) {
            case 1:
                gamestate->flags->isWhiteTurn = fenChar == 'w';
                counter++;
                continue;
            case 2:
                switch (fenChar) {
                    case 'K':
                        gamestate->flags->whiteKCastle = true;
                        break;
                    case 'Q':
                        gamestate->flags->whiteQCastle = true;
                        break;
                    case 'k':
                        gamestate->flags->blackKCastle = true;
                        break;
                    case 'q':
                        gamestate->flags->blackQCastle = true;
                        break;
                }
                counter++;
                continue;
            case 3:
                if (fenChar == '-') {
                    continue;
                }
                gamestate->flags->canEnPassant = true;
                char* tempChar = getFenPart(fenChar, &counter);
                convertCharToPosition(tempChar, gamestate->enPassantPosition);
                counter++;
                continue;
            case 4:
                char* part = getFenPart(fen, &counter);
                gamestate->counter->halfMove = atoi(part);
                counter++;
                continue;
            case 5:
                char* part = getFenPart(fen, &counter);
                gamestate->counter->fullMove = atoi(part);
                counter++;
                continue;
        }
        
        if (islower(fenChar) { // lower case piece means black piece
            uint64_t temp = pow(2, 63 - counter);
            bitboards->black += temp;
            bitboards->occupancy += temp;
            setPieceOnBitboard(fenChar, temp, bitboards);
        } else if (isUpper(fenChar) { // upper case piece means white piece
            uint64_t temp = pow(2, 63 -counter);
            bitboards->white += temp;
            bitboards->occupancy += temp;
            setPieceOnbitboard(fenChar, temp, bitboards);
        } else if (isdigit(fenChar) { // digit means that many spaces are empty
            counter += fenChar - '0';
        } else if (isspace(fenChar) { // if space, next part
            partCounter++;
        } else if (fenChar = '/') { // used to say next line. not needed here
            counter++;
            continue;
        }
        counter++;
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
void setPieceOnBitboard(char fenChar, uint64_t value, Bitboards* bitboards) {
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
            fprintf(stderr, "Error in converting fen notation to bitboards: character '%s' not defined.\n", fenChar);
            exit(ERROR_FEN_CHAR_NOT_DEFINED);            
    }
}

/**
 * fetches the next part of the fen notation. This is marked by a space
 *
 * @param fen: the complete fen notation char pointer
 * @param startIndex: pointer of counter, representing the first index of the next fen notation part. THIS MUST NOT BE SPACE!
 *
 * @exit ERROR_FETCHING_FEN_PART: If after completing fetching, the next character is not a space
 *
 */
char* getFenPart(char* fen, short* startIndex) {
    char part[128];
    int i;

    for (i = 0; i < 128 || !isspace(fen[*startIndex+i]; i++) {
        if (isspace(fen[*startIndex+i]) {
            break;
        }
        part[i] = fen[*startIndex+i];
    }
    if (!isspace(fen[*startIndex+i]) { // sanity check if the next character is space. Can happen if part is longer than 128 character
        fprintf(stderr, "Error in trying to fetch next fen notation part.\n", fenChar);
        exit(ERROR_FETCHING_FEN_PART);
    }
    *startIndex += i; // add i -1 to the current counter. i-1 because space is needed in other parts of the fen notation loading
    return part;
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
void convertCharArrayToPosition(char fenChar*, Position* position) {
    short temp;
    char rank = *fenChar;
    char file = *(fenChar+1)

    if (isalpha(rank) {
        temp = rank - 'a';
        if (temp > 8 || temp < 1) {
            fprintf(stderr, "Error in converting char position to Position struct: character file '%s' does not convert to a number between 1 and 8.\n", rank);
            exit(ERROR_CANNOT_CONVERT_POSITION);
        }
        position->file = temp;
    } else {
        fprintf(stderr, "Error in converting char position to Position struct: character '%s' not defined.\n", rank);
        exit(ERROR_CANNOT_CONVERT_POSITION);
    }

    if (isdigit(file) {
        temp = file - '0';
        if (temp > 8 || temp < 0) {
            fprintf(stderr, "Error in converting char position to Position struct: character rank '%s' does not convert to a number between 1 and 8.\n", file);
            exit(ERROR_CANNOT_CONVERT_POSITION);
        }
        position->rank = temp;
    } else {
        fprintf(stderr, "Error in converting char position to Position struct: character '%s' not defined.\n", file);
        exit(ERROR_CANNOT_CONVERT_POSITION);
    }
}

/*
 * converts a char array into an integer
 *
 * @param charNumber: pointer to array to convert to number
 *
 * @exit ERROR_CONVERTING_CHARARRAY_TO_INT: if there is an unexpected character in the array
 *
 */
int convertCharArrayToInt(char* charNumber) {
    for (int i = 0; charNumber[i] != '\0'; i++) {
        if (!isdigit(charNumber[i]) { // character that is not a digit
            fprintf(stderr, "Error in converting char array to int: '%s' of '%s' is not a digit.\n" charNumber[i], charNumber);
            exit(ERROR_CONVERTING_CHARARRAY_TO_INT);
        }
    }
    return atoi(charNumber);
}
