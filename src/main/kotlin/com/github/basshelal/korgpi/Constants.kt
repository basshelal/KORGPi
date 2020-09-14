package com.github.basshelal.korgpi

import javax.sound.sampled.AudioFormat

const val SAMPLE_RATE = 44100F

val DEFAULT_FORMAT = AudioFormat(SAMPLE_RATE, 16, 2, true, true)
val EASY_FORMAT = AudioFormat(SAMPLE_RATE, 8, 1, true, true)
