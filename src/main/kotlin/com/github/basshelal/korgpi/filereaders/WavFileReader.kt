package com.github.basshelal.korgpi.filereaders

import com.github.basshelal.korgpi.MinMax
import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.extensions.string
import com.github.basshelal.korgpi.extensions.toArray
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.log.logE
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class WavFileReader(filePath: String) {

    private val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(File(filePath))
    val format: AudioFormat = audioInputStream.format

    inline val sampleSizeInBytes: Int get() = format.sampleSizeInBits / 8

    val intRange: MinMax<Int>
    val floatRange: MinMax<Float>

    init {
        val buffer = FourBytes(format.isBigEndian)
        for (byteIndex in 0 until sampleSizeInBytes) {
            buffer.put(Byte.MIN_VALUE)
        }
        logE(buffer)
        val minInt = buffer.int
        logE(buffer.buffer.string())
        val minFloat = buffer.float
        logE(buffer.buffer.string())

        buffer.clear()
        for (byteIndex in 0 until sampleSizeInBytes) {
            buffer[byteIndex] = Byte.MAX_VALUE
        }
        logE(buffer)
        val maxInt = buffer.int
        logE(buffer.buffer.string())
        val maxFloat = buffer.float
        logE(buffer.buffer.string())

        this.intRange = MinMax(minInt, maxInt)
        this.floatRange = MinMax(minFloat, maxFloat)

    }


    val byteArray = ByteArray(sampleCount.I * sampleSizeInBytes * format.channels).also {
        audioInputStream.read(it, 0, it.size)
        audioInputStream.close()
    }

    val sampleCount: Long
        get() {
            val total: Long = (audioInputStream.frameLength * format.frameSize * 8) / format.sampleSizeInBits
            return total / format.channels
        }

    // Range from -1.0F to 1.0F
    val floatArray: IntArray
        get() {
            val audioBytes = byteArray
            val buffer = ByteBuffer.allocate(4) // 32 bits => 4 bytes
            buffer.order(if (format.isBigEndian) ByteOrder.BIG_ENDIAN else ByteOrder.LITTLE_ENDIAN)
            var byteIndex = 0 // index in audioBytes
            return IntArray(sampleCount.I) { sampleIndex: Int ->
                buffer.clear()
                for (i in 0 until sampleSizeInBytes) {
                    buffer.put(audioBytes[byteIndex++])
                }
                while (buffer.hasRemaining()) buffer.put(0)
                buffer.position(0)
                logD(buffer.toArray().joinToString())
                val int = buffer.int
                //  logD(int)
                //  bytesToConvert.fill(0)
                //     logD(int)
                int
            }
        }


    val data: List<FloatArray>
        get() {
            val audioBytes = byteArray
            var audioBytesIndex = 0

            val list = List(format.channels) { FloatArray(sampleCount.I) }
            val buffer = FourBytes(format.isBigEndian)

            for (sample in 0 until sampleCount.I) {
                for (channel in 0 until format.channels) {
                    buffer.clear()
                    for (byteIndex in 0 until sampleSizeInBytes) {
                        buffer[byteIndex] = audioBytes[audioBytesIndex]
                        audioBytesIndex++
                    }
                    list[channel][sample] = buffer.float
                }
            }
            // TODO: 15/01/2021 Not yet range normalized to be from -1.0F to 1.0F
            return list
        }

}

data class FourBytes(val isBigEndian: Boolean) : Iterable<Byte?> {

    private var cursor = if (isBigEndian) 0 else 3
    private var _0: Byte? = null
    private var _1: Byte? = null
    private var _2: Byte? = null
    private var _3: Byte? = null

    private val byteBuffer = ByteBuffer.allocateDirect(4)
            .order(if (this.isBigEndian) ByteOrder.BIG_ENDIAN else ByteOrder.LITTLE_ENDIAN)

    fun toArray(): Array<Byte?> = arrayOf(_0, _1, _2, _3)

    val buffer: ByteBuffer = byteBuffer.asReadOnlyBuffer()

    // TODO: 16/01/2021 Delete! Absolute put should not be allowed!
    operator fun set(index: Int, value: Byte) {
        when (index) {
            0 -> _0 = value
            1 -> _1 = value
            2 -> _2 = value
            3 -> _3 = value
            else -> throw IllegalStateException("Index must be between 0 and 3 inclusive, provided $index")
        }
    }

    infix fun put(value: Byte): FourBytes {
        require(cursor in 0..3) { "Cursor reached limit trying to put $value! Use reset() to reset cursor" }
        this[cursor] = value
        if (isBigEndian) cursor++ else cursor--
        return this
    }

    operator fun get(index: Int): Byte? {
        return when (index) {
            0 -> _0
            1 -> _1
            2 -> _2
            3 -> _3
            else -> throw IllegalStateException("Index must be between 0 and 3 inclusive, provided $index")
        }
    }

    fun reset() {
        cursor = if (isBigEndian) 0 else 3
    }

    fun clear() {
        _0 = null
        _1 = null
        _2 = null
        _3 = null
        reset()
    }

    val float: Float
        get() {
            return Float.fromBits(this.int)
        }

    val int: Int
        get() {
            val bitCount = this.count { it != null } * 8
            if (isBigEndian) { // left to right
                if (bitCount == 0) return 0
                // TODO: 16/01/2021 Clean up later
                val a = (_0?.I?.shl(bitCount - 8)) ?: 0
                val b = (_1?.I?.shl(bitCount - 16)) ?: 0
                val c = (_2?.I?.shl(bitCount - 24)) ?: 0
                val d = (_3?.I) ?: 0
                val x = a or b or c or d
                return x
            } else { // right to left
                if (bitCount == 0) return 0
                // TODO: 16/01/2021 Clean up later
                val a = (_3?.I?.shl(bitCount - 8)) ?: 0
                val b = (_2?.I?.shl(bitCount - 16)) ?: 0
                val c = (_1?.I?.shl(bitCount - 24)) ?: 0
                val d = (_0?.I) ?: 0
                val x = a or b or c or d
                return x
            }
        }

    override fun iterator(): Iterator<Byte?> {
        return object : Iterator<Byte?> {

            private var index = 0

            override fun hasNext(): Boolean = index < 4

            override fun next(): Byte? {
                val result = this@FourBytes[index]
                index++
                return result
            }

        }
    }

    override fun toString(): String = "[$_0, $_1, $_2, $_3]"
}