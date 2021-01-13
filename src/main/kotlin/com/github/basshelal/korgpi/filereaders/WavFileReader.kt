package com.github.basshelal.korgpi.filereaders

import com.github.basshelal.korgpi.extensions.F
import com.github.basshelal.korgpi.extensions.I
import java.io.File
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.pow

class WavFileReader(filePath: String) {

    private val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(File(filePath))
    val format: AudioFormat = audioInputStream.format

    inline val sampleSizeInBytes: Int get() = format.sampleSizeInBits / 8

    val sampleCount: Long
        get() {
            val total: Long = (audioInputStream.frameLength * format.frameSize * 8) / format.sampleSizeInBits
            return total / format.channels
        }

    val byteArray: ByteArray
        get() = ByteArray(sampleCount.I * sampleSizeInBytes * format.channels).also {
            audioInputStream.read(it, 0, it.size)
        }

    // Range from -1.0F to 1.0F
    val floatArray: FloatArray
        get() {
            // TODO: 13/01/2021 Unsure about code below
            val audioBytes = byteArray
            val sampleBytes = IntArray(sampleSizeInBytes)
            var bytesIndex = 0 // index in audioBytes
            val audioSamples = FloatArray(sampleCount.I) { sampleIndex: Int ->
                // collect sample byte in big-endian order
                if (format.isBigEndian) {
                    // bytes start with MSB
                    for (byteIndex in 0 until sampleSizeInBytes) {
                        sampleBytes[byteIndex] = audioBytes[bytesIndex++].toInt()
                    }
                } else {
                    // bytes start with LSB
                    var j = sampleSizeInBytes - 1
                    while (j >= 0) {
                        sampleBytes[j] = audioBytes[bytesIndex++].toInt()
                        if (sampleBytes[j] != 0) j = j + 0
                        j--
                    }
                }
                // get integer value from bytes
                var ival = 0
                for (j in 0 until sampleSizeInBytes) {
                    ival += sampleBytes[j]
                    if (j < sampleSizeInBytes - 1) ival = ival shl 8
                }
                val ratio = (format.sampleSizeInBits - 1).F.pow(2.0F)
                return@FloatArray ival.F / ratio
            }
            return audioSamples
        }

}