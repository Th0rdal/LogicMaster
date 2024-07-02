#include "utility/debugging.h"

void printBoard(uint64_t board) {
    printf("0x%llx\n", board);
    for (int i = 63; i >= 0; --i) {
        uint64_t mask = 1ULL << i;
        int bit = (board & mask) ? 1 : 0;
        printf("%d", bit);
        if (i%8==0) {
            printf("\n");
        }
    }
    printf("\n");
}

void debugGamestate(Gamestate* gamestate) {
    printf("occupancy\n");
    printBoard(gamestate->bitboards.occupancy);

    printf("colors white\n");
    printBoard(gamestate->bitboards.color[1]);
    printf("colors black\n");
    printBoard(gamestate->bitboards.color[0]);
    
    printf("pawn:\n");
    printBoard(gamestate->bitboards.pawn);
    printf("rook:\n");
    printBoard(gamestate->bitboards.rook);
    printf("knight:\n");
    printBoard(gamestate->bitboards.knight);
    printf("bishop:\n");
    printBoard(gamestate->bitboards.bishop);
    printf("queen:\n");
    printBoard(gamestate->bitboards.queen);
    printf("king:\n");
    printBoard(gamestate->bitboards.king);
    
    printf("full move counter: %d\n", gamestate->counters.fullMove);
    printf("half move counter: %d\n", gamestate->counters.halfMove);

    printf("\nflags\n");
    printf("turn: %s\n", gamestate->flags.isWhiteTurn ? "true" : "false");
    printf("white king castle: %d\n", gamestate->flags.kCastle[1]);
    printf("white queen castle: %d\n", gamestate->flags.qCastle[1]);
    printf("black king castle: %d\n", gamestate->flags.kCastle[0]);
    printf("black queen castle: %d\n", gamestate->flags.qCastle[0]);
    printf("can en passant: %d\n", gamestate->flags.canEnPassant);

    printf("en Passant Position: %c", gamestate->enPassantPosition.rank + 64);
    printf("%d", gamestate->enPassantPosition.file);
    
}
