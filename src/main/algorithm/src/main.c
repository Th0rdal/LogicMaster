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
void preprocessing(INPUTFORMAT inputFormat, char* inputString, Gamestate* gamestate)  {
    switch (inputFormat) {
        case FEN:
            fenToBitboard(inputString, gamestate);
            break;
        default:
            throwError(ERROR_NOINPUT, "Input format was not specified or given");
            break;
    }
}

void postprocessing() {
    return;
}

int main(int argc, char *argv[]) {
    
    INPUTFORMAT inputFormat = NONE;
    char *inputString = NULL;

    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0) {
            printf("Usage: %s [options]\n", argv[0]);
            printf("Options:\n");
            printf("\t-h, --help\tShow this help message\n");
            return 0;
        } else if (strcmp(argv[i], "-ifen") == 0 || strcmp(argv[i], "--InputFenNotation") == 0) {
            inputFormat = FEN;
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

    //for test purposes
    strcat(inputString, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    inputFormat = FEN;

    Gamestate* gamestate = gamestateInit();
    
    //start "main loop"
    preprocessing(inputFormat, inputString, gamestate);
    // minimax aufrufen. der soll pre post processing selbst machen
    postprocessing();
    return 0;
}
