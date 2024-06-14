#include "minimax/preprocessing.h"

void appendStateToArray(Gamestate* calculatedStates, Gamestate* toAppend, short* counter, short* maxCount) {
    if (*counter == *maxCount) {
        maxCount += MOVES_INCREMENT;
        Gamestate* temp = (Gamestate*) realloc(calculatedStates, sizeof(Gamestate) * *maxCount);
        if (temp == NULL) {
            throwError(ERROR_MEMORY_REALLOC_FAILED, "Error in reallocating memory: function appendStateToArray");
        }
        calculatedStates = temp;
    }
}
void calculateMoves(Gamestate* gamestat) {
    /* 
     * TODO:
     * create all pawn moves
     * create all rook moves
     * create all knight moves
     * create all bishop moves
     * create all queen moves
     * */

    Gamestate* calculatedStates = (Gamestate*) malloc(sizeof(Gamestate)*AVERAGE_MOVES);
    if (calculatedStates == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error in allocating memory: function calculateMoves");
    }
    short calculatedStatesCounter = 0;
    short maxCounter = AVERAGE_MOVES;


}
    
void minimax_preprocessing(short maxDepth, Gamestate* gamestate) {
    /*
     * threading:
     * give each thread their own thread struct with id and result pointer
     * thread creates own Gamestate* array
     * thread returns pointer to array
     * main joins all threads. then uses result to create new threads until at max depth
     * end result gets saved in endStates. 
     *
     * create all king moves
     * 
     * */
}
