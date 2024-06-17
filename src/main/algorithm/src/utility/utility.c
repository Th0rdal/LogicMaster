#include "utility/utility.h"

/*
 * converts a char array into an integer. Allows negative number
 *
 * @param charNumber: pointer to array to convert to number
 *
 * @return: integer representing the char array
 * @exit ERROR_CONVERTING_CHARARRAY_TO_INT: if there is an unexpected character in the array
 *
 */
int convertCharArrayToInt(char* charNumber) {
    if (charNumber[0] != '-' && !isdigit(charNumber[0])) {
       throwError(ERROR_CONVERTING_CHARARRAY_TO_INT,"Error in converting char array to int: '%c' of '%s' is not a digit or '-' sign\n", charNumber[0], charNumber);
    }
    for (int i = 1; charNumber[i] != '\0'; i++) {
        if (!isdigit(charNumber[i])) { // character that is not a digit
            throwError(ERROR_CONVERTING_CHARARRAY_TO_INT, "Error in converting char array to int: '%c' of '%s' is not a digit.\n", charNumber[i], charNumber);
        }
    }
    return atoi(charNumber);
}

/*
 * reverses a uint64_t bitwise
 *
 * @param: number to reverse
 *
 * @return: number bitwise reversed
 *
 */
uint64_t reverseBitboard(uint64_t bitboard) {
    uint64_t reverse = bitboard;
    reverse = ((reverse & 0x5555555555555555) << 1) | ((reverse >> 1) & 0x5555555555555555);
    reverse = ((reverse & 0x3333333333333333) << 2) | ((reverse >> 2) & 0x3333333333333333);
    reverse = ((reverse & 0x0f0f0f0f0f0f0f0f) << 4) | ((reverse >> 4) & 0x0f0f0f0f0f0f0f0f);
    reverse = ((reverse & 0x00ff00ff00ff00ff) << 8) | ((reverse >> 8) & 0x00ff00ff00ff00ff);
    reverse = ((reverse & 0x0000ffff0000ffff) << 16) | ((reverse >> 16) & 0x0000ffff0000ffff);
    reverse = (reverse << 32) | (reverse >> 32);
    return reverse;
}
