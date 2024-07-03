#include "main.h"

/**
 * Brings input into a normed format (gamestate) before giving it to the algorithm
 *
 * @param inputFormat: the INPUTFORMAT enum representing the format of the input string
 * @param inputString: the given input string that will be converted into a gamestate
 * @param gamestate: the gamestate to save the convertion in
 *
 * @exit: NOINPUT: if the input enum is not set or not recognized 
 * */
void preprocessing(command_args* args, Gamestate* gamestate)  {
    if (strcmp(args->inputString, "") == 0) {
        throwError(ERROR_NOINPUT, "Error: missing the inputString representing the board");
    }
    switch (args->inputFormat) {
        case FEN:
            fenToBitboard(args->inputString, gamestate);
            break;
        default:
            throwError(ERROR_NOINPUT, "Error: input format was not specified or given");
            break;
    }
}

void postprocessing(command_args* args, Gamestate* gamestate) {
    if (!args->onlyPossibleMoves) {
        char moveString[20];
        printMove(&gamestate->move, moveString);
        printf("%s\n", moveString);
    } else {
        printf("\n"); //print empty line so gui does not think first possible move is chosen move
    }
    if (args->opponentMovesFlag || args->onlyPossibleMoves) {
        printAllPossibleMoves(args, gamestate);
    }
}

int main(int argc, char *argv[]) {
    command_args* args = (command_args*)malloc(sizeof(command_args));
    if (args == NULL) {
        throwError(ERROR_MEMORY_MALLOC_FAILED, "Error: failed to allocate memory for the command args");
    }
    
    args->inputFormat = NONE;
    args->opponentMovesFlag = false; 
    strcpy(args->inputString, "");
    args->maxDepth = 4;
    args->maxThreads = -1;
    args->onlyPossibleMoves = false;

    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0) {
            printf("Usage: %s [options]\n", argv[0]);
            printf("Options:\n");
            printf("\t-h, --help\t\t\tShow this help message\n");
            printf("\t-opm, --only-possible-moves\tOnly returns all possible moves (first line empty because of gui formatting\n");
            printf("\t-om, --opponents-moves\t\tPrints all moves the opponent can make after the algorithm move\n");
            printf("\t-ifen, --input-fen-notation\tDefine the input as given in fen notation\n");
            printf("\t-md, --max-depth\t\tDefine the max depth to calculate to. Use between 1 and 4. 5 and higher needs more than 40GB memory and will likely fail unless enough memory is provided\n");
            printf("\t-mt, --max-threads\t\tDefine the maximal amount of threads the program may use for each generation adn evaluation. If not given, the program will calculate it based on the system\n");
            return 0;
        } else if (strcmp(argv[i], "-opm") == 0 || strcmp(argv[i], "--only-possible-moves") == 0) {
            args->onlyPossibleMoves = true;
            args->maxDepth = 1;
        } else if (strcmp(argv[i], "-om") == 0 || strcmp(argv[i], "--opponents-moves") == 0) {
            args->opponentMovesFlag = true;
            continue;
        } else if (strcmp(argv[i], "-ifen") == 0 || strcmp(argv[i], "--input-fen-notation") == 0) {
            args->inputFormat = FEN;
            continue;
        } else if (strcmp(argv[i], "-md") == 0 || strcmp(argv[i], "--max-depth") == 0) {
            if (i + 1 < argc) {
                args->maxDepth = atoi(argv[++i]);
            } else {
                throwError(ERROR_NOINPUT, "Error: max depth flag set but no amount given");
            }
            continue;
        } else if (strcmp(argv[i], "-mt") == 0 || strcmp(argv[i], "--max-threads") == 0) {
            if (i + 1 < argc) {
                args->maxThreads = atoi(argv[++i]);
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
            strcpy(args->inputString, argv[i]);
            continue;
        }
    }

    //printf("Starting LogicMaster...\n\n");
    Gamestate* gamestate = gamestateInit();

    preprocessing(args, gamestate);
    //debugGamestate(gamestate);
    
    gamestate->flags.isWhiteTurn = !gamestate->flags.isWhiteTurn;
    Gamestate* moveGamestate = minimax(args, gamestate);

    postprocessing(args, moveGamestate);
    return 0;
}
