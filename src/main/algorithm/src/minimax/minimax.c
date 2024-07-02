#include "minimax/minimax.h"

/*!
 * gets the number of processors for maxThreads
 *
 *  @return: maxThreads
 */
static inline int setupMaxThreads(int maxThreads) {
    if (maxThreads > 0) {
        return maxThreads;
    }
    SYSTEM_INFO sysinfo;
    GetSystemInfo(&sysinfo);
    int maxThreadsCalc = sysinfo.dwNumberOfProcessors;
    if (maxThreadsCalc < 1) {
        throwWarning(WARNING_COULD_NOT_RETRIEVE_CORE_NUMBER, "Warning: failed to retrieve the amount of cores the processor has for max worker threads. Set max worker threads to 1");
        maxThreadsCalc = 1;
    }
    return maxThreadsCalc;
}

Gamestate* minimax(short maxDepth, int maxThreadsInput, Gamestate* gamestate) {
    int maxThreads = setupMaxThreads(maxThreadsInput);
   
    initializeTree();
    preprocessing_start(maxDepth, maxThreads, gamestate);
    evaluation_start(maxThreads);
    return evaluateLastGamestate(tree->head);
}
