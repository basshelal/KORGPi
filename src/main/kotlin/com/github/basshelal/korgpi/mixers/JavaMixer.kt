package com.github.basshelal.korgpi.mixers

import com.github.basshelal.korgpi.audio.JAudioDevice
import com.github.basshelal.korgpi.extensions.allLines
import com.github.basshelal.korgpi.extensions.simpleClassName
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

object JavaMixer {

    fun initialize() {

    }

    fun allJMixers(): List<Mixer> = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }

    fun allAudioDevices(): List<JAudioDevice> = allJMixers().map { JAudioDevice(it) }

    fun allUsableAudioDevices(): List<Mixer> = allJMixers().filter { it.allLines().isNotEmpty() }

    fun allLines(): List<Line> = allJMixers().flatMap { it.allLines() }

    fun allDataLines(): List<DataLine> = allLines().filterIsInstance<DataLine>()

    fun allWriteableDataLines(): List<SourceDataLine> = allLines().filterIsInstance<SourceDataLine>()

    fun allReadableDataLines(): List<TargetDataLine> = allLines().filterIsInstance<TargetDataLine>()

    fun allMidiDevices(): List<MidiDevice> = MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }

    fun midiOutDevices(): List<MidiDevice> = allMidiDevices().filter { it.simpleClassName == "MidiOutDevice" }

    fun midiInDevices(): List<MidiDevice> = allMidiDevices().filter { it.simpleClassName == "MidiInDevice" }

}