#ifndef MOCKEXIT_H
#define MOCKEXIT_H

#include <setjmp.h>
#include <windows.h>

extern jmp_buf exit_jump;
extern int exit_status;

void mockExit(int status);
//_Noreturn void throwError(int errorCode, char* format, ...);

#endif 

