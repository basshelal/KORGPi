@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.extensions.allLines
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer

object JavaAudio {
    inline fun allMixers(): List<Mixer> = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }

    inline fun allLines(): List<Line> = allMixers().flatMap { it.allLines() }

    inline fun allDataLines(): List<DataLine> = allLines().filter { it is DataLine }.map { it as DataLine }
}