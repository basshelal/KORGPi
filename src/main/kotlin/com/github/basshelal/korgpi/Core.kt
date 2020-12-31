package com.github.basshelal.korgpi

import kotlin.math.PI

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

// Annotation for functions to indicate real time critical code
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION)
annotation class RealTimeCritical

const val TWOPI: Double = 2.0 * PI
const val UINT_MAX: Long = 4294967295L
const val USHORT_MAX: Int = 65535