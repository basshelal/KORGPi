package com.github.basshelal.korgpi

import javax.sound.sampled.AudioFormat

const val SAMPLE_RATE = 44100

val WAVE_DEFAULT_FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 1, 2, 44100F, false)
val EASY_FORMAT = AudioFormat(SAMPLE_RATE.F, 8, 1, true, true)
