#include "minimax/preprocessing.h"

static inline void updateWorkCounter(MoveGenerationThreadPool *pool, const int value);
static inline void destroyGenerationThreadPool(MoveGenerationThreadPool *pool);
static __attribute__((always_inline)) inline void addToQueue(Gamestate* newGamestate, GamestateTreeNode* node, Queue* queue, int* movesAddedToQueue);
int calculateMoves(Gamestate* gamestate, Queue* queue, GamestateTreeNode* node);

DWORD WINAPI generationWorker(LPVOID lpParam) {
    MoveGenerationThreadPool* pool = (MoveGenerationThreadPool *)lpParam;
    Queue* workQueue = pool->queue;
    int counter = 0;
    
    while (!pool->shutdown && pool->workCounter > 0) {
        counter = 0;
        Gamestate* gamestate = dequeue(workQueue);
        if (gamestate == NULL) {
            continue;
        }
        if (gamestate->config.node == NULL) {
            criticalCreateGamestateTreeNode(gamestate);
        }
        if (gamestate->config.depth == pool->maxDepth) {
            updateWorkCounter(pool, counter-1);
            continue;
        }

        counter = calculateMoves(gamestate, workQueue, gamestate->config.node);
        if (!gamestate->move.flags.checkmate && counter == 0) {
            gamestate->move.flags.draw = true;
        }
        updateWorkCounter(pool, counter-1);
    }
    return 0;
}

/*!
 * calculates all possible moves for a given gamestate, create new gamestate and save it in the queue
 *
 * @param gamestate: the gamestate to use for the calculations
 * @param queue: the queue to push the results in
 * @param node: the GamestateTreeNode of the parent
 *
 * @return: number of gamestates added to the queue
 *
 * */
int calculateMoves(Gamestate* gamestate, Queue* queue, GamestateTreeNode* node) {

    int side = !gamestate->flags.isWhiteTurn ? 1 : 0;
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
            Gamestate* newGamestate;
            bool returnValue;
            if ((movePositions+j)->rank == rankBitboards.pawnPromotionRank[!gamestate->flags.isWhiteTurn]) {
                newGamestate = gamestateInit();
                returnValue = gamestateMakeMoveInternal(gamestate, newGamestate, PAWN, (piecePositions+i), (movePositions+j), QUEEN);
                if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);

                newGamestate = gamestateInit();
                returnValue = gamestateMakeMoveInternal(gamestate, newGamestate, PAWN, (piecePositions+i), (movePositions+j), ROOK);
                if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);

                newGamestate = gamestateInit();
                returnValue = gamestateMakeMoveInternal(gamestate, newGamestate, PAWN, (piecePositions+i), (movePositions+j), KNIGHT);
                if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);

                newGamestate = gamestateInit();
                returnValue = gamestateMakeMoveInternal(gamestate, newGamestate, PAWN, (piecePositions+i), (movePositions+j), BISHOP);
                if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);
            } else {
                newGamestate = gamestateInit();
                returnValue = gamestate_makeMove(gamestate, newGamestate, PAWN, (piecePositions+i), (movePositions+j));  
                if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);
            }
            
        }
        free(movePositions);
        movePositions = NULL;
    }
    free(piecePositions);
    piecePositions = NULL;

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
            if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);
 
        }
        free(movePositions);
        movePositions = NULL;
    }
    free(piecePositions);
    piecePositions = NULL;

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
            if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);
        }
        free(movePositions);
        movePositions = NULL;
    }
    free(piecePositions);
    piecePositions = NULL;

   
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
            if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);
            
        }
        free(movePositions);
        movePositions = NULL;
    }
    free(piecePositions);
    piecePositions = NULL;

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
            if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);
        }
        free(movePositions);
        piecePositions = NULL;
    }
    free(piecePositions);
    piecePositions = NULL;

    //king
    board = gamestate->bitboards.king & gamestate->bitboards.color[side];
    count = __builtin_popcountll(board);
    piecePositions = getAllPiecePositions(board, count);
    for (int i = 0; i < count; i++) {
        // possibleMovesAfterCheck is 0xFF unless the king is in check          
        moveBoard = kingMoves(gamestate, (piecePositions+i)) & gamestate->bitboards.possibleMovesAfterCheck;
        if (moveBoard == 0) { // no moves possible
            continue;
        }
        count2 = __builtin_popcountll(moveBoard);
        movePositions = getAllPiecePositions(moveBoard, count2);
        for (int j = 0; j < count2; j++) {
            Gamestate* newGamestate = gamestateInit();
            bool returnValue = gamestate_makeMove(gamestate, newGamestate, KING, (piecePositions+i), (movePositions+j));  
            if (returnValue) addToQueue(newGamestate, node, queue, &movesAddedToQueue);
        }    
        free(movePositions);
        movePositions = NULL;
    }
    free(piecePositions);
    piecePositions = NULL;

    return movesAddedToQueue;
}
   
/*!
 * handles everything needed to add a new gamestate to the work queue.
 * Supposed to be inlined at location
 * */
static __attribute__((always_inline)) inline void addToQueue(Gamestate* newGamestate, GamestateTreeNode* node, Queue* queue, int* movesAddedToQueue) {
    newGamestate->config.parent = node;
    if (newGamestate->config.node == NULL) {
        createGamestateTreeNode(newGamestate);
    }
    enqueue(queue, newGamestate);
    (*movesAddedToQueue)++;
} 

void preprocessing_start(command_args* args, Gamestate* gamestate) {
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
    
    MoveGenerationThreadPool* genPool = moveGenerationThreadPoolInit(args);
    genPool->workCounter++;
    enqueue(genPool->queue, gamestate);
    for (int i = 0; i < args->maxThreads; i++) {
        genPool->threads[i] = CreateThread(
            NULL,
            0,
            generationWorker,
            genPool,
            0,
            NULL 
        );
    }
    
    DWORD waitResult = WaitForMultipleObjects(genPool->maxThreads, genPool->threads, TRUE, MAX_WAITTIME);
    if (waitResult == WAIT_TIMEOUT) {
        genPool->shutdown = true;
        throwError(ERROR_THREADS_TIMEOUT, "Error: generation thread took too long and triggered the MAX_WAITTIME timeout. Threads were shutdown");
    }
    destroyGenerationThreadPool(genPool); 
    genPool = NULL;
}

MoveGenerationThreadPool* moveGenerationThreadPoolInit(command_args* args) {
    MoveGenerationThreadPool* pool = (MoveGenerationThreadPool*)malloc(sizeof(MoveGenerationThreadPool));
    if (pool == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for moveGenerationThreadPool");
    }
    pool->maxThreads = args->maxThreads;
    pool->threads = (HANDLE*) malloc(args->maxThreads * sizeof(HANDLE));
    if (pool->threads == NULL) {
       throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for move generation worker threads"); 
    }
    pool->queue = queueInit(args->maxDepth);
    InitializeCriticalSection(&pool->lock);
  
    pool->shutdown = false;
    pool->workCounter = 0;
    pool->maxDepth = args->maxDepth;

    return pool;
}

/*!
 * updates the workCounter variable of the given Mpool struct
 *
 * @param pool: the pool variable with the workCounter variable
 * @param value: the update value for workCounter. The value gets added to workCounter
 *
 * */
static inline void updateWorkCounter(MoveGenerationThreadPool *pool, const int value) {
    EnterCriticalSection(&pool->lock);
    pool->workCounter += value;
    LeaveCriticalSection(&pool->lock);
}

/*!
 * destroys the MoveGenerationThreadPool struct and all needed fields
 *
 * @param pool: the struct to destroy
 *
 * */
static inline void destroyGenerationThreadPool(MoveGenerationThreadPool *pool) {
    DeleteCriticalSection(&pool->lock);
    for (int i = 0; i < pool->maxThreads; i++) {
        CloseHandle(pool->threads[i]);
    }
    free(pool->threads);
    destroyQueue(pool->queue);
    free(pool);
}

