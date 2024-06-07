#ifndef DEBUGGING_H
#define DEBUGGING_H

#include <stdio.h>
#include <stdint.h>

#include "structs/gamestate.h"

extern void printBoard(uint64_t board);
extern void debugGamestate(Gamestate* gamestate);

#endif
