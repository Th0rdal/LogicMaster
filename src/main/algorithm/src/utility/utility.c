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

