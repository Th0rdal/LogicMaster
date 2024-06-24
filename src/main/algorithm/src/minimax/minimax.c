#include "minimax/minimax.h"

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

void minimax(short maxDepth, Gamestate* gamestate) {
    //TODO
    //preprocessing, core, postprocessing
}
