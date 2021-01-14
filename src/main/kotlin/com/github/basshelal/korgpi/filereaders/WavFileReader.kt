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

}