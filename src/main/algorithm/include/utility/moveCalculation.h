#ifndef MOVECALCULATION_H
#define MOVECALCULATION_H

#include <stdint.h>

#include "utility\utility.h"
#include "structs\position.h"
#include "structs\gamestate.h"




/*!
 * calculates all possible moves a given pawn can do.
 *
 * @param gamestate: the current gamestate
 * @param position: position of the pawn as a struct
 * @param forWhitePiece: the color of the piece
 *
 * @return bitboard representing all possible moves
 *
 * */ 
uint64_t pawnMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece);
/*!
 * calculates all possible moves a given pawn can do.
 * A wrapper function calling pawnMovesWithSideSelection.
 *
 * @param gamestate: the current gamestate
 * @param position: position of the pawn as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */ 
__attribute__((always_inline)) inline uint64_t pawnMoves(const Gamestate* gamestate, const Position* position) {
    return pawnMovesWithSideSelection(gamestate, position, !gamestate->flags.isWhiteTurn);
}

/*!
 * calculates all possible moves a given rook can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the rook as a struct
 * @param forWhitePiece: the color of the piece
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t rookMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece);
/*!
 * calculates all possible moves a given rook can do
 * A wrapper function calling pawnMovesWithSideSelection.
 *
 * @param gamestate: the current gamestate
 * @param position: position of the rook as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
__attribute__((always_inline)) inline uint64_t rookMoves(const Gamestate* gamestate, const Position* position) {
    return rookMovesWithSideSelection(gamestate, position, !gamestate->flags.isWhiteTurn);
}

/*!
 * calculates all possible moves a given knight can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the knight as a struct
 * @param forWhitePiece: the color of the piece
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t knightMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece);
/*!
 * calculates all possible moves a given knight can do
 * A wrapper function calling pawnMovesWithSideSelection.
 *
 * @param gamestate: the current gamestate
 * @param position: position of the knight as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
__attribute__((always_inline)) inline uint64_t knightMoves(const Gamestate* gamestate, const Position* position) {
    return knightMovesWithSideSelection(gamestate, position, !gamestate->flags.isWhiteTurn);
}

/*!
 * calculates all possible moves a given bishop can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the bishop as a struct
 * @param forWhitePiece: the color of the piece
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t bishopMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece);
/*!
 * calculates all possible moves a given bishop can do
 * A wrapper function calling pawnMovesWithSideSelection.
 *
 * @param gamestate: the current gamestate
 * @param position: position of the bishop as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
__attribute__((always_inline)) inline uint64_t bishopMoves(const Gamestate* gamestate, const Position* position) {
    return bishopMovesWithSideSelection(gamestate, position, !gamestate->flags.isWhiteTurn);
}

/*!
 * calculates all possible moves a given queen can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the queen as a struct
 * @param forWhitePiece: the color of the piece
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t queenMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece);
/*!
 * calculates all possible moves a given queen can do
 * A wrapper function calling pawnMovesWithSideSelection.
 *
 * @param gamestate: the current gamestate
 * @param position: position of the queen as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
__attribute__((always_inline)) inline uint64_t queenMoves(const Gamestate* gamestate, const Position* position) {
    return queenMovesWithSideSelection(gamestate, position, !gamestate->flags.isWhiteTurn);
}

/*!
 * calculates all possible moves a given king can do
 *
 * @param gamestate: the current gamestate
 * @param position: position of the king as a struct
 * @param forWhitePiece: the color of the piece
 *
 * @return bitboard representing all possible moves
 *
 * */
uint64_t kingMovesWithSideSelection(const Gamestate* gamestate, const Position* position, const bool forWhitePiece);
/*!
 * calculates all possible moves a given king can do
 * A wrapper function calling pawnMovesWithSideSelection.
 *
 * @param gamestate: the current gamestate
 * @param position: position of the king as a struct
 *
 * @return bitboard representing all possible moves
 *
 * */
__attribute__((always_inline)) inline uint64_t kingMoves(const Gamestate* gamestate, const Position* position) {
    return kingMovesWithSideSelection(gamestate, position, !gamestate->flags.isWhiteTurn);
}

/*!
 * returns bitboards containing all checking pieces + all block possibilities + all king moves for the current gamestate color.
 * If checkForWhite is true the return value will be the position of black pieces attacking the square.
 * If this is 0 while king is in check => checkmate.
 *
 * also saves the bitboards into config of the gamestate
 *
 * @param gamestate: the gamestate to check
 * @param kingPosition: the position of the king that needs to be checked
 * @param checkForWhite: true for white, false for black
 *
 * @return: bitboard with all possible king moves, piece captures or block opportunities
 *
 * */
uint64_t squareAttacked(const Gamestate* gamestate, const Position* position, const bool checkForWhite);

/*!
 * makes a move and checks if the move is legal, by seeing if the king is under attack afterwards.
 * this is only filling a new gamestate with the most essential values to check.
 * 
 * @param gamestate: the gamestate to check
 * @param piece: the piece that made the move
 * @param piecePosition: the starting position of the piece
 * @param movePosition: the end position of the piece
 *
 * return: true if the move is legal, otherwise false
 *
 * */
uint64_t createPossibleMovesAfterCheck(const Gamestate* gamestate, const Position* kingPosition, const bool forWhitePiece);

/*!
 * calculates all squares that have pieces that attack the given position
 *
 * @param gamestate: the gamestate to check
 * @param position: the position to check
 *
 * @return: bitboard with a 1 on every position a piece that is attacking the defined position is standing
 *
 * @warning ERROR_PIECE_NOT_SELECTED: if there was no piece or the NO_PIECE value given to the function instead of a piece
 * */
bool makeMoveAndCheckLegal(const Gamestate* gamestate, const enum PIECE piece, const Position* piecePosition, const Position* movePosition);

#endif
