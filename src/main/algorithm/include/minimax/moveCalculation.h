#ifndef MOVECALCULATION_H
#define MOVECALCULATION_H

#include <stdint.h>

#include "utility\utility.h"
#include "structs\position.h"
#include "structs/gamestate.h"

uint64_t pawnMoves(Gamestate* gamestate, Position* position);
uint64_t rookMoves(Gamestate* gamestate, Position* position);
uint64_t knightMoves();
uint64_t bishopMoves();
uint64_t queenMoves();
uint64_t kingMoves();

#endif
