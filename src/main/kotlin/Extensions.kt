package uk.whitecrescent.instrumentdigitizer

inline fun <reified T : Throwable> ignoreException(func: () -> Any) {
    try {
        func()
    } catch (e: Throwable) {
        if (e !is T) throw e
    }
}