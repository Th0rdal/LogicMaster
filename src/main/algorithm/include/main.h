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

#endif
