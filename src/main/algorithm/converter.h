#ifndef CONVERTER_H
#define CONVERTER_H

#include <math.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "errorCodes.h"

uint64_t* fenToBitboard(char* fen, Gamestate* gamestate);

#endif
