package com.github.basshelal.korgpi

import javax.sound.sampled.AudioFormat
import kotlin.math.PI

const val HALF_PI = PI / 2.0

const val SAMPLE_RATE = 44100

val MAX_AMPLITUDE = Byte.MAX_VALUE.D
val MIN_AMPLITUDE = Byte.MIN_VALUE.D

val WAVE_DEFAULT_FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 1, 2, 44100F, false)
val EASY_FORMAT = AudioFormat(SAMPLE_RATE.F, 8, 1, true, true)
