#ifndef MOVECALCULATION_H
#define MOVECALCULATION_H

#include <stdint.h>

#include "utility\utility.h"
#include "structs\position.h"
#include "structs/gamestate.h"

uint64_t pawnMoves();
uint64_t rookMoves();
uint64_t knightMoves();
uint64_t bishopMoves();
uint64_t queenMoves();
uint64_t kingMoves();

//only for testing purposes. should not be used outside of tests
uint64_t antiDiagonalSlide(Gamestate* gamestate, short position);
uint64_t diagonalSlide(Gamestate* gamestate, short position);
#endif
