package com.github.basshelal.korgpi.mixers

import com.github.basshelal.korgpi.audio.AudioDevice
import com.github.basshelal.korgpi.audio.JMixer
import com.github.basshelal.korgpi.extensions.allLines
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

// For Audio Devices
object AudioMixer {

    fun allJMixers(): List<JMixer> = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }

    fun allAudioDevices(): List<AudioDevice> = allJMixers().map { AudioDevice(it) }

    fun allUsableAudioDevices(): List<Mixer> = allJMixers().filter { it.allLines().isNotEmpty() }

    fun allLines(): List<Line> = allJMixers().flatMap { it.allLines() }

    fun allDataLines(): List<DataLine> = allLines().filterIsInstance<DataLine>()

    fun allWriteableDataLines(): List<SourceDataLine> = allLines().filterIsInstance<SourceDataLine>()

    fun allReadableDataLines(): List<TargetDataLine> = allLines().filterIsInstance<TargetDataLine>()

}