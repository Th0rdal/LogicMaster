#include "minimax/preprocessing.h"

/*void appendStateToArray(Gamestate* calculatedStates, Gamestate* toAppend, short* counter, short* maxCount) {
    if (*counter == *maxCount) {
        maxCount += MOVES_INCREMENT;
        Gamestate* temp = (Gamestate*) realloc(calculatedStates, sizeof(Gamestate) * *maxCount);
        if (temp == NULL) {
            throwError(ERROR_MEMORY_REALLOC_FAILED, "Error in reallocating memory: function appendStateToArray");
        }
        calculatedStates = temp;
    }
}
*/
void* worker(void* args) {
    MoveGenerationThreadPool* pool = (MoveGenerationThreadPool *)args;
    Queue workQueue = pool->queue;
    int counter = 0;

    while (!pool->shutdown && pool->workCounter > 0) {
        Gamestate* gamestate = dequeue(&workQueue);
        if (gamestate->config.depth == pool->maxDepth) {
            enqueue(&pool->results, gamestate);
        }
        counter = calculateMoves(gamestate, &workQueue);
        updateWorkCounter(pool, counter-1);
    }
     
}

int calculateMoves(Gamestate* gamestate, Queue* queue) {

    bool side = gamestate->flags.isWhiteTurn;
    uint64_t board, moveBoard;
    short count, count2;
    Position* piecePositions, *movePositions;
    int movesAddedToQueue = 0;

    /* what happens here (same concept for each piece):
     *  get all pawns of the color whose turn it currently is
     *  count all pawns of the color
     *  get all Positions of the pawns of the color
     *  for each position, first create all possible moves (& with possibleMovesAfterCheck which should be FF unless the king is in check)
     *      if king in check, this will remove all moves that would leave you in check afterwards
     *      if there are no moves, go to next position
     *      count all moves
     *      get the position of all moves
     *      for each move
     *          create a new gamestate
     *          make the move
     *          if the move is legal, add to queue and increase movesAddedCounter
     */
    board = gamestate->bitboards.pawn & gamestate->bitboards.color[side];
    count = __builtin_popcountll(board);
    piecePositions = getAllPiecePositions(board, count);
    for (int i = 0; i < count; i++) {
        // possibleMovesAfterCheck is FF unless the king is in check          
        moveBoard = pawnMoves(gamestate, (piecePositions+i)) & gamestate->bitboards.possibleMovesAfterCheck;
        if (moveBoard == 0) { // no moves possible
            continue;
        }
        count2 = __builtin_popcountll(moveBoard);
        movePositions = getAllPiecePositions(moveBoard, count2);
        for (int j = 0; j < count2; j++) {
            Gamestate* newGamestate = gamestateInit();
            bool returnValue = gamestate_makeMove(gamestate, newGamestate, PAWN, (piecePositions+i), (movePositions+j));  
            if (returnValue) {
                enqueue(queue, newGamestate);
                movesAddedToQueue++;
            } 
        }
    }
    
    // rook calculations
    board = gamestate->bitboards.rook & gamestate->bitboards.color[side];
    count = __builtin_popcountll(board);
    piecePositions = getAllPiecePositions(board, count);
    for (int i = 0; i < count; i++) {
        // possibleMovesAfterCheck is FF unless the king is in check          
        moveBoard = rookMoves(gamestate, (piecePositions+i)) & gamestate->bitboards.possibleMovesAfterCheck;
        if (moveBoard == 0) { // no moves possible
            continue;
        }
        count2 = __builtin_popcountll(moveBoard);
        movePositions = getAllPiecePositions(moveBoard, count2);
        for (int j = 0; j < count2; j++) {
            Gamestate* newGamestate = gamestateInit();
            bool returnValue = gamestate_makeMove(gamestate, newGamestate, ROOK, (piecePositions+i), (movePositions+j));  
            if (returnValue) {
                enqueue(queue, newGamestate);
                movesAddedToQueue++;
            } 
        }
    }   

    // knight
    board = gamestate->bitboards.knight & gamestate->bitboards.color[side];
    count = __builtin_popcountll(board);
    piecePositions = getAllPiecePositions(board, count);
    for (int i = 0; i < count; i++) {
        // possibleMovesAfterCheck is FF unless the king is in check          
        moveBoard = knightMoves(gamestate, (piecePositions+i)) & gamestate->bitboards.possibleMovesAfterCheck;
        if (moveBoard == 0) { // no moves possible
            continue;
        }
        count2 = __builtin_popcountll(moveBoard);
        movePositions = getAllPiecePositions(moveBoard, count2);
        for (int j = 0; j < count2; j++) {
            Gamestate* newGamestate = gamestateInit();
            bool returnValue = gamestate_makeMove(gamestate, newGamestate, KNIGHT, (piecePositions+i), (movePositions+j));  
            if (returnValue) {
                enqueue(queue, newGamestate);
                movesAddedToQueue++;
            } 
        }
    }
    
    //bishop
    board = gamestate->bitboards.bishop & gamestate->bitboards.color[side];
    count = __builtin_popcountll(board);
    piecePositions = getAllPiecePositions(board, count);
    for (int i = 0; i < count; i++) {
        // possibleMovesAfterCheck is FF unless the king is in check          
        moveBoard = bishopMoves(gamestate, (piecePositions+i)) & gamestate->bitboards.possibleMovesAfterCheck;
        if (moveBoard == 0) { // no moves possible
            continue;
        }
        count2 = __builtin_popcountll(moveBoard);
        movePositions = getAllPiecePositions(moveBoard, count2);
        for (int j = 0; j < count2; j++) {
            Gamestate* newGamestate = gamestateInit();
            bool returnValue = gamestate_makeMove(gamestate, newGamestate, BISHOP, (piecePositions+i), (movePositions+j));  
            if (returnValue) {
                enqueue(queue, newGamestate);
                movesAddedToQueue++;
            } 
        }
    }

    //queen
    board = gamestate->bitboards.queen & gamestate->bitboards.color[side];
    count = __builtin_popcountll(board);
    piecePositions = getAllPiecePositions(board, count);
    for (int i = 0; i < count; i++) {
        // possibleMovesAfterCheck is FF unless the king is in check          
        moveBoard = queenMoves(gamestate, (piecePositions+i)) & gamestate->bitboards.possibleMovesAfterCheck;
        if (moveBoard == 0) { // no moves possible
            continue;
        }
        count2 = __builtin_popcountll(moveBoard);
        movePositions = getAllPiecePositions(moveBoard, count2);
        for (int j = 0; j < count2; j++) {
            Gamestate* newGamestate = gamestateInit();
            bool returnValue = gamestate_makeMove(gamestate, newGamestate, QUEEN, (piecePositions+i), (movePositions+j));  
            if (returnValue) {
                enqueue(queue, newGamestate);
                movesAddedToQueue++;
            } 
        }
    }

    //king
    board = gamestate->bitboards.king & gamestate->bitboards.color[side];
    count = __builtin_popcountll(board);
    piecePositions = getAllPiecePositions(board, count);
    for (int i = 0; i < count; i++) {
        // possibleMovesAfterCheck is FF unless the king is in check          
        moveBoard = kingMoves(gamestate, (piecePositions+i)) & gamestate->bitboards.possibleMovesAfterCheck;
        if (moveBoard == 0) { // no moves possible
            continue;
        }
        count2 = __builtin_popcountll(moveBoard);
        movePositions = getAllPiecePositions(moveBoard, count2);
        for (int j = 0; j < count2; j++) {
            Gamestate* newGamestate = gamestateInit();
            bool returnValue = gamestate_makeMove(gamestate, newGamestate, KING, (piecePositions+i), (movePositions+j));  
            if (returnValue) {
                enqueue(queue, newGamestate);
                movesAddedToQueue++;
            } 
        }
    }

    return movesAddedToQueue;
}
    
void minimax_preprocessing(short maxDepth, int maxThreads, Gamestate* gamestate) {
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
    
    MoveGenerationThreadPool pool = moveGenerationThreadPoolInit(maxDepth, maxThreads);    


    for (int i = 0; i < maxThreads; i++) {
        pthread_create(&pool.threads[i], NULL, worker, (void *)&pool);
    }

}
