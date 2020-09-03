@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi

import javax.sound.sampled.Line
import javax.sound.sampled.Mixer

inline val Number.I: Int
    get() = this.toInt()

inline val Number.D: Double
    get() = this.toDouble()

inline val Number.F: Float
    get() = this.toFloat()

inline val Number.L: Long
    get() = this.toLong()

inline val now: Long get() = System.currentTimeMillis()

inline fun <reified T : Throwable> ignoreException(func: () -> Any) {
    try {
        func()
    } catch (e: Throwable) {
        if (e !is T) throw e
    }
}

inline fun Mixer.allLines(): List<Line> {
    return this.sourceLines.plus(this.targetLines).asList()
}