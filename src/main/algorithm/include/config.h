#ifndef CONFIG_H
#define CONFIG_H

#define MAX_WAITTIME 10000 // sets the maximal time the program waits for the threads to finish the calculations
#define QUEUE_RESIZE
#define STACK_RESIZE

#include <stdbool.h>

typedef enum {
    FEN,
    NONE
}INPUTFORMAT; 

typedef struct command_args {
    INPUTFORMAT inputFormat;
    bool opponentMovesFlag;
    char inputString[1024];
    int maxDepth;
    int maxThreads;
}command_args;

#endif
