#ifndef MOVECALCULATION_H
#define MOVECALCULATION_H

#include <stdint.h>

#include "utility\utility.h"
#include "structs\position.h"
#include "structs\gamestate.h"

uint64_t pawnMoves(Gamestate* gamestate, Position* position);
uint64_t rookMoves(Gamestate* gamestate, Position* position);
uint64_t knightMoves(Gamestate* gamestate, Position* position);
uint64_t bishopMoves(Gamestate* gamestate, Position* position);
uint64_t queenMoves(Gamestate* gamestate, Position* position);
uint64_t kingMoves(Gamestate* gamestate, Position* position);
uint64_t squareAttacked(Gamestate* gamestate, Position* position);
uint64_t createPossibleMovesAfterCheck(Gamestate* gamestate, Position* kingPosition);
bool makeMoveAndCheckLegal(Gamestate* gamestate, enum PIECE piece, Position* piecePosition, Position* movePosition);

#endif
