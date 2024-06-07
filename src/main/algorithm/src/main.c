#include "main.h"

int main(int argc, char *argv[]) {
    
    bool iFenFlag = false;
    char *inputString = NULL;

    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0) {
            printf("Usage: %s [options]\n", argv[0]);
            printf("Options:\n");
            printf("\t-h, --help\tShow this help message\n");
            return 0;
        } else if (strcmp(argv[i], "-ifen") == 0 || strcmp(argv[i], "--InputFenNotation") == 0) {
            iFenFlag = true; 
        } else if (argv[i][0] == '-') {
            fprintf(stderr, "Unknown option: %s\n", argv[i]);
            fprintf(stderr, "Use '%s --help' to see the list of available options.\n", argv[0]);
            return 1;
        } else {
            inputString = argv[i];
            break;
        }
    }

    printf("Starting LogicMaster...\n\n");
    Gamestate gamestate = gamestateInit();
    char* fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    fenToBitboard(fen, &gamestate);

    return 0;
}
