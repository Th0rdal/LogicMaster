#ifndef MOCKEXIT_H
#define MOCKEXIT_H

#include <setjmp.h>
#include <windows.h>
#include <stdbool.h>
#include <stdio.h>
#include "unity.h"

extern jmp_buf exit_jump;
extern int exit_status;
extern bool shouldThrow;

void setupThrow();

#endif 

