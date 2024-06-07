#include "utility/utility.h"

/*
 * converts a char array into an integer
 *
 * @param charNumber: pointer to array to convert to number
 *
 * @return: integer representing the char array
 * @exit ERROR_CONVERTING_CHARARRAY_TO_INT: if there is an unexpected character in the array
 *
 */
int convertCharArrayToInt(char* charNumber) {
    for (int i = 0; charNumber[i] != '\0'; i++) {
        if (!isdigit(charNumber[i])) { // character that is not a digit
            fprintf(stderr, "Error in converting char array to int: '%c' of '%s' is not a digit.\n", charNumber[i], charNumber);
            exit(ERROR_CONVERTING_CHARARRAY_TO_INT);
        }
    }
    return atoi(charNumber);
}
