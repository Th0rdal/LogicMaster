#ifndef MAIN_H
#define MAIN_H

#include <stdio.h>
#include <string.h>
                           
// utility functions
#include "utility/converter.h"
#include "utility/debugging.h"

// structs
#include "structs/gamestate.h"
#include "structs/bitboards.h"

// algorithm includes
#include "minimax/minimax.h"

typedef enum {
    FEN,
    NONE
}INPUTFORMAT; 

/**
 * Brings input into a normed format (gamestate) before giving it to the algorithm
 *
 * @param inputFormat: the INPUTFORMAT enum representing the format of the input string
 * @param inputString: the given input string that will be converted into a gamestate
 * @param gamestate: the gamestate to save the convertion in
 *
 * @exit: NOINPUT: if the input enum is not set or not recognized 
 * */
void preprocessing(INPUTFORMAT inputFormat, char* inputString, Gamestate* gamestate);

#endif
