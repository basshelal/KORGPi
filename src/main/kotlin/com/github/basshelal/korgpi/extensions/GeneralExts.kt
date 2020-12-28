@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import java.nio.FloatBuffer
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

inline val UByte.I: Int
    get() = this.toInt()

inline val now: Long get() = System.currentTimeMillis()

inline val nowNanos: Long get() = System.nanoTime()

inline fun convertScale(oldScaleMin: Int, oldScaleMax: Int,
                        newScaleMin: Int, newScaleMax: Int,
                        oldValueToConvert: Int): Int {
    val oldScaleRange: Int = oldScaleMax - oldScaleMin
    val newScaleRange: Int = newScaleMax - newScaleMin
    return ((oldValueToConvert - oldScaleMin) * newScaleRange / oldScaleRange) + newScaleMin
}

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

inline fun addOnSystemShutdownCallback(crossinline func: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(Thread { func() })
}

inline fun <reified E : Enum<E>> EnumSet(): EnumSet<E> = EnumSet.noneOf(E::class.java)

inline fun <reified E : Enum<E>> EnumSet(e: E): EnumSet<E> = EnumSet.of(e)

inline operator fun FloatBuffer.set(index: Int, value: Float) {
    this.put(index, value)
}

inline fun FloatBuffer.forEach(func: (value: Float) -> Unit) {
    for (i: Int in (0 until capacity())) {
        func(this[i])
    }
}

inline fun FloatBuffer.forEachIndexed(func: (value: Float, index: Int) -> Unit) {
    for (i: Int in (0 until capacity())) {
        func(this[i], i)
    }
}

inline fun FloatBuffer.updateEach(func: (value: Float, index: Int) -> Float) {
    for (i: Int in (0 until capacity())) {
        this[i] = func(this[i], i)
    }
}

inline fun FloatBuffer.fillWith(value: Float): FloatBuffer {
    this.forEachIndexed { _, index ->
        this[index] = value
    }
    return this
}

inline fun FloatBuffer.zero(): FloatBuffer = this.fillWith(0F)