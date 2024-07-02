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
    int maxThreads = -1;

    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0) {
            printf("Usage: %s [options]\n", argv[0]);
            printf("Options:\n");
            printf("\t-h, --help\tShow this help message\n");
            printf("\t-ifen, --input-fen-notation\tDefine the input as given in fen notation\n");
            printf("\t-md, --max-depth\tDefine the max depth to calculate to. Use between 1 and 4. 5 and higher needs more than 40GB memory and will likely fail unless enough memory is provided\n");
            printf("\t-mt, --max-threads\tDefine the maximal amount of threads the program may use for each generation adn evaluation. If not given, the program will calculate it based on the system\n");
            return 0;
        } else if (strcmp(argv[i], "-ifen") == 0 || strcmp(argv[i], "--input-fen-notation") == 0) {
            inputFormat = FEN;
            continue;
        } else if (strcmp(argv[i], "-md") == 0 || strcmp(argv[i], "--max-depth") == 0) {
            if (i + 1 < argc) {
                maxDepth = atoi(argv[++i]);
            } else {
                throwError(ERROR_NOINPUT, "Error: max depth flag set but no amount given");
            }
            continue;
        } else if (strcmp(argv[i], "-mt") == 0 || strcmp(argv[i], "--max-threads") == 0) {
            if (i + 1 < argc) {
                maxThreads = atoi(argv[++i]);
            } else {
                throwError(ERROR_NOINPUT, "Error: max threads flag set but no amount given");
            }
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

    Gamestate* moveGamestate = minimax(maxDepth, maxThreads, gamestate);

    postprocessing(moveGamestate);
    return 0;
}
