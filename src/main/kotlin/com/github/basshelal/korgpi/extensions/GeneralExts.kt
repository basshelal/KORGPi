@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import java.nio.ByteBuffer
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

inline val Number.S: Short
    get() = this.toShort()

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
    this.forEachIndexed { _, index -> this[index] = value }
    return this
}

inline fun FloatBuffer.zero(): FloatBuffer = this.fillWith(0F)

inline fun FloatBuffer.toArray(): FloatArray = FloatArray(this.capacity()) { this[it] }

inline operator fun ByteBuffer.set(index: Int, value: Byte) {
    this.put(index, value)
}

inline fun ByteBuffer.forEach(func: (value: Byte) -> Unit) {
    for (i: Int in (0 until capacity())) {
        func(this[i])
    }
}

inline fun ByteBuffer.forEachIndexed(func: (value: Byte, index: Int) -> Unit) {
    for (i: Int in (0 until capacity())) {
        func(this[i], i)
    }
}

inline fun ByteBuffer.updateEach(func: (value: Byte, index: Int) -> Byte) {
    for (i: Int in (0 until capacity())) {
        this[i] = func(this[i], i)
    }
}

inline fun ByteBuffer.fillWith(value: Byte): ByteBuffer {
    this.forEachIndexed { _, index -> this[index] = value }
    return this
}

inline fun ByteBuffer.toArray(): ByteArray = ByteArray(this.capacity()) { this[it] }

inline fun ByteBuffer.zero(): ByteBuffer = this.fillWith(0)

inline fun ByteArray.toByteBuffer(): ByteBuffer = ByteBuffer.wrap(this)

inline fun ByteBuffer.subBuffer(beginIndex: Int, endIndex: Int): ByteBuffer {
    var from = beginIndex
    var to = endIndex

    if (from < 0) from = 0
    if (from > this.capacity()) from = this.capacity()
    if (to < 0) to = 0
    if (to > this.capacity()) to = this.capacity()
    if (from > to) from = to

    return ByteArray(to - from) { this.get(it + from) }.toByteBuffer()
}