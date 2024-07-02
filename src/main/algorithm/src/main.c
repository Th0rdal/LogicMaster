#include "main.h"

void preprocessing(INPUTFORMAT inputFormat, char* inputString, Gamestate* gamestate)  {
    if (strcmp(inputString, "") == 0) {
        throwError(ERROR_NOINPUT, "Error: missing the inputString representing the board");
    }
    switch (inputFormat) {
        case FEN:
            fenToBitboard(inputString, gamestate);
            break;
        default:
            throwError(ERROR_NOINPUT, "Error: input format was not specified or given");
            break;
    }
}

void postprocessing(Gamestate* gamestate) {
    char moveString[20];
    printMove(&gamestate->move, moveString);
    printf("%s", moveString);
}

int main(int argc, char *argv[]) {
    
    INPUTFORMAT inputFormat = NONE;
    char inputString[1024] = "";
    int maxDepth = 4;
    bool algorithmColorWhite = false;

    for (int i = 1; i < argc; i++) {
        printf("string: %s\n", argv[i]);
        if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0) {
            printf("Usage: %s [options]\n", argv[0]);
            printf("Options:\n");
            printf("\t-h, --help\tShow this help message\n");
            return 0;
        } else if (strcmp(argv[i], "-ifen") == 0 || strcmp(argv[i], "--input-fen-notation") == 0) {
            inputFormat = FEN;
            continue;
        } else if (strcmp(argv[i], "-sc") == 0 || strcmp(argv[i], "--set-color") == 0) {
            if (i + 1 < argc) {
                if (strcmp(argv[++i], "w") == 0) {
                    algorithmColorWhite = true;
                } else if (strcmp(argv[++i], "b") == 0) {
                    algorithmColorWhite = false;
                }
            } else {
                throwError(ERROR_NOINPUT, "Error: no color argument was given");
            }
            continue;
        } else if (strcmp(argv[i], "-scw") == 0 || strcmp(argv[i], "--set-color-white") == 0) {
            algorithmColorWhite = true;
            continue;
        } else if (strcmp(argv[i], "-scb") == 0 || strcmp(argv[i], "--set-color-black") == 0) {
            algorithmColorWhite = false;
            continue;
        } else if (strcmp(argv[i], "-md") == 0 || strcmp(argv[i], "--maxDepth") == 0) {
            if (i + 1 < argc) {
                maxDepth = atoi(argv[++i]);
            } else {
                throwError(ERROR_NOINPUT, "Error: no depth number was given");
            }
            continue;
        } else if (argv[i][0] == '-') {
            char string[1024];
            strcpy(string, "Unknown option: %s\n");
            strcat(string, "Use '%s --help' to see the list of available options.\n");
            throwError(ERROR_UNKNOWN_ARGUMENT, string, argv[i], argv[0]);
            return 1;
        } else {
            strcpy(inputString, argv[i]);
            continue;
        }
    }

    printf("Starting LogicMaster...\n\n");
    Gamestate* gamestate = gamestateInit();

    preprocessing(inputFormat, inputString, gamestate);

    Gamestate* moveGamestate = minimax(maxDepth, algorithmColorWhite, gamestate);

    postprocessing(moveGamestate);
    return 0;
}
