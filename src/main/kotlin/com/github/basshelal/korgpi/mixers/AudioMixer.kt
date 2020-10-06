package com.github.basshelal.korgpi.mixers

import com.github.basshelal.korgpi.extensions.allLines
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

// For Audio Devices
object AudioMixer {

    fun allAudioDevices(): List<Mixer> = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }

    fun allUsableAudioDevices(): List<Mixer> = allAudioDevices().filter { it.allLines().isNotEmpty() }

    fun allLines(): List<Line> = allAudioDevices().flatMap { it.allLines() }

    fun allDataLines(): List<DataLine> = allLines().filterIsInstance<DataLine>()

    fun allWriteableDataLines(): List<SourceDataLine> = allLines().filterIsInstance<SourceDataLine>()

    fun allReadableDataLines(): List<TargetDataLine> = allLines().filterIsInstance<TargetDataLine>()

}