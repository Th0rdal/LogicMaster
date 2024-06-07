#include <stdio.h>
#include <string.h>

#include "main.h"
#include "bitboards.h"

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

int main(int argc, char *argv[]) {
    
    int iFenFlag = FALSE;
    char *inputString = NULL;

    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0) {
            printf("Usage: %s [options]\n", argv[0]);
            printf("Options:\n");
            printf("\t-h, --help\tShow this help message\n");
            return 0;
        } else if (strcmp(argv[i], "-ifen") == 0 || strcmp(argv[i], "--InputFenNotation") == 0) {
            iFenFlag = TRUE; 
        } else if (argv[i][0] == '-') {
            fprintf(stderr, "Unknown option: %s\n", argv[i]);
            fprintf(stderr, "Use '%s --help' to see the list of available options.\n", argv[0]);
            return 1;
        } else {
            inputString = argv[i];
            break;
        }
    }

    for (int i = 0; i < knightMoveDictionary.size; i++) {
        printBoard(knightMoveDictionary.boards[i]);
    }

    return 0;
}
