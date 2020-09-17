package com.github.basshelal.korgpi

// Very similar to AutoCloseable except we have more control over it
interface Destructible {
    // Doesn't actually destroy but instead releases resources to be ready for destruction
    fun beforeDestroy()
}

// A nicer try with resources but no exception handling
inline fun <reified T : Destructible> T.runAndDestroy(block: (T) -> Unit) {
    block(this)
    this.beforeDestroy()
}