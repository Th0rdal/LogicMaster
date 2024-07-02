#include "minimax/minimax.h"

bool algorithmColorWhite = false;

/*!
 * gets the number of processors for maxThreads
 *
 *  @return: maxThreads
 */
static inline int setupMaxThreads() {
    SYSTEM_INFO sysinfo;
    GetSystemInfo(&sysinfo);
    int maxThreads = sysinfo.dwNumberOfProcessors;
    if (maxThreads < 1) {
        throwWarning(WARNING_COULD_NOT_RETRIEVE_CORE_NUMBER, "Warning: failed to retrieve the amount of cores the processor has for max worker threads. Set max worker threads to 1");
        maxThreads = 1;
    }
    return maxThreads;
}

Gamestate* minimax(short maxDepth, bool algorithmColorWhite, Gamestate* gamestate) {
    //TODO
    //preprocessing, core, postprocessing
    algorithmColorWhite = algorithmColorWhite;
    int maxThreads = setupMaxThreads();
   
    initializeTree();
    preprocessing_start(maxDepth, maxThreads, gamestate);
    evaluation_start(maxThreads);
    return evaluateLastGamestate(tree->head);
}
