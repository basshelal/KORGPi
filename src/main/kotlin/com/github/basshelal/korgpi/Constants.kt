package com.github.basshelal.korgpi

import com.github.basshelal.korgpi.extensions.F
import javax.sound.sampled.AudioFormat

const val SAMPLE_RATE = 44100

val WAVE_DEFAULT_FORMAT = AudioFormat(44100F, 16, 2, true, true)
val EASY_FORMAT = AudioFormat(SAMPLE_RATE.F, 8, 1, true, true)
