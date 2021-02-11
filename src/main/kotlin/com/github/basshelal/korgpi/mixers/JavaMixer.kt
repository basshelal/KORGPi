package com.github.basshelal.korgpi.mixers

import com.github.basshelal.korgpi.audio.JAudioDevice
import com.github.basshelal.korgpi.audio.ReadableLine
import com.github.basshelal.korgpi.audio.WritableLine
import com.github.basshelal.korgpi.extensions.allLines
import com.github.basshelal.korgpi.extensions.simpleClassName
import com.github.basshelal.korgpi.utils.JLine
import com.github.basshelal.korgpi.utils.JMixer
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

object JavaMixer {

    fun initialize() {

    }

    val jMixers: List<JMixer> get() = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }

    val audioDevices: List<JAudioDevice> get() = jMixers.map { JAudioDevice(it) }

    val defaultAudioDevice: JAudioDevice? get() = audioDevices.find { it.jMixerInfo.name.contains("default") }

    val usableAudioDevices: List<JMixer> get() = jMixers.filter { it.allLines().isNotEmpty() }

    val jLines: List<JLine> get() = jMixers.flatMap { it.allLines() }

    val allDataLines: List<DataLine> get() = jLines.filterIsInstance<DataLine>()

    val sourceDataLines: List<SourceDataLine> get() = jLines.filterIsInstance<SourceDataLine>()

    val writableLines: List<WritableLine> get() = sourceDataLines.map { WritableLine(it) }

    val targetDataLines: List<TargetDataLine> get() = jLines.filterIsInstance<TargetDataLine>()

    val readableLines: List<ReadableLine> get() = targetDataLines.map { ReadableLine(it) }

    val midiDevices: List<MidiDevice> get() = MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }

    val midiOutDevices: List<MidiDevice> get() = midiDevices.filter { it.simpleClassName == "MidiOutDevice" }

    val midiInDevices: List<MidiDevice> get() = midiDevices.filter { it.simpleClassName == "MidiInDevice" }

    fun start(onProcess: () -> Boolean) {
        // start the audio thread, how do we make it run once every frame????
        // how can we even know what a frame is??
    }

}