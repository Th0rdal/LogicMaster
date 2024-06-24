#ifndef CONVERTER_H
#define CONVERTER_H

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <ctype.h>

#include "errors.h"
#include "structs/bitboards.h"
#include "structs/gamestate.h"

/*!
 * Converts a fen notation char array into a bitboard struct and writes it into the bitboard struct
 * Expects en Passant position in format a-h1-8 e.g., a5, e3
 *
 * @param fen: fen notation char array
 * @param bitboards: Bitboards struct pointer to empty bitboard
 *
 * */
void fenToBitboard(char* fen, Gamestate* gamestate);

#endif
