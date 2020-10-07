package com.github.basshelal.korgpi.audio

import javax.sound.sampled.AudioFormat

const val SAMPLE_RATE = 48000F

object Formats {
    val default = AudioFormat(SAMPLE_RATE, 16, 2, true, true)
}