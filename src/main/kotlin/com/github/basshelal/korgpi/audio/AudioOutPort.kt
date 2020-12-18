package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.Key
import com.github.basshelal.korgpi.extensions.B
import com.github.basshelal.korgpi.extensions.D
import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.extensions.set
import com.github.basshelal.korgpi.mixers.JackMixer
import org.jaudiolibs.jnajack.JackPort
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class AudioOutPort(var jackPort: JackPort) {

    var sampleRate: Double = Double.MIN_VALUE

    @RealTimeCritical
    fun process() {
        if (sampleRate == Double.MIN_VALUE) sampleRate = JackMixer.jackClient.sampleRate.D

        for (i: Int in (0 until jackPort.buffer.capacity())) {
            jackPort.buffer[i] = Random.nextInt(from = -128, until = 127).B
        }
    }

    fun sine(size: Int): ByteArray {
        val interval = SAMPLE_RATE.D / 440
        return ByteArray(size) {
            (sin((2.0 * PI * it) / interval) * 1.0F).B
        }
    }

}

fun playNote(noteNumber: Int): ByteArray {
    val noteFrequency: Double = Key.fromNumber(noteNumber).frequency
    val interval = SAMPLE_RATE.D / noteFrequency
    return ByteArray(SAMPLE_RATE.I) {
        (sin((2.0 * PI * it) / interval) * 127.0).B
    }
}



