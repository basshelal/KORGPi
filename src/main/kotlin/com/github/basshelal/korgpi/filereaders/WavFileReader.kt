package com.github.basshelal.korgpi.filereaders

import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.extensions.toArray
import com.github.basshelal.korgpi.log.logD
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
            val byteBuffer = FourBytes()

            for (sample in 0 until sampleCount.I) {
                for (channel in 0 until format.channels) {
                    byteBuffer.clear()
                    for (byteIndex in 0 until sampleSizeInBytes) {
                        byteBuffer[byteIndex] = audioBytes[audioBytesIndex]
                        audioBytesIndex++
                    }
                    list[channel][sample] = byteBuffer.float(format.isBigEndian)
                }
            }
            // TODO: 15/01/2021 Not yet range normalized to be from -1.0F to 1.0F
            return list
        }

}

data class FourBytes(private var _0: Byte = 0,
                     private var _1: Byte = 0,
                     private var _2: Byte = 0,
                     private var _3: Byte = 0) : Iterable<Byte> {

    private val byteBuffer = ByteBuffer.allocateDirect(4)

    fun toArray(): ByteArray = byteArrayOf(_0, _1, _2, _3)

    operator fun set(index: Int, value: Byte) {
        require(index in 0..3) { "Index must be between 0 and 3 inclusive, provided $index" }
        when (index) {
            0 -> _0 = value
            1 -> _1 = value
            2 -> _2 = value
            3 -> _3 = value
        }
    }

    operator fun get(index: Int): Byte {
        require(index in 0..3) { "Index must be between 0 and 3 inclusive, provided $index" }
        return when (index) {
            0 -> _0
            1 -> _1
            2 -> _2
            3 -> _3
            else -> throw IllegalStateException()
        }
    }

    fun clear() {
        _0 = 0
        _1 = 0
        _2 = 0
        _3 = 0
    }

    private fun put(bigEndian: Boolean) =
            byteBuffer.clear()
                    // Don't know why it works correctly only when they're inverted :/
                    .order(if (bigEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN)
                    .put(_0).put(_1).put(_2).put(_3).position(0)


    fun float(bigEndian: Boolean): Float {
        return this.put(bigEndian).float
    }

    fun int(bigEndian: Boolean): Int {
        return this.put(bigEndian).int
    }

    override fun iterator(): Iterator<Byte> {
        return object : Iterator<Byte> {

            private var index = 0

            override fun hasNext(): Boolean = index < 4

            override fun next(): Byte {
                val result = this@FourBytes[index]
                index++
                return result
            }

        }
    }

    override fun toString(): String = "[$_0, $_1, $_2, $_3]"
}