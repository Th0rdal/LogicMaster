#ifndef CONVERTER_H
#define CONVERTER_H

#include <math.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "errorCodes.h"
#include "structs/bitboards.h"
#include "structs/gamestate.h"

void fenToBitboard(char* fen, Gamestate* gamestate);

#endif
