@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import java.util.EnumSet

inline val Number.I: Int
    get() = this.toInt()

inline val Number.D: Double
    get() = this.toDouble()

inline val Number.F: Float
    get() = this.toFloat()

inline val Number.L: Long
    get() = this.toLong()

inline val Number.B: Byte
    get() = this.toByte()

inline val now: Long get() = System.currentTimeMillis()

inline val nowNanos: Long get() = System.nanoTime()

inline val Any?.simpleClassName: String get() = this?.javaClass?.simpleName ?: "null"

inline fun ignoreExceptions(printStackTrace: Boolean = false, func: () -> Unit) =
        ignoreException<Throwable>(printStackTrace, func)

inline fun <reified T : Throwable> ignoreException(printStackTrace: Boolean = false, func: () -> Unit) {
    try {
        func()
    } catch (e: Throwable) {
        if (e !is T) throw e
        else if (printStackTrace) e.printStackTrace()
    }
}

inline fun <reified E : Enum<E>> EnumSet(): EnumSet<E> = EnumSet.noneOf(E::class.java)