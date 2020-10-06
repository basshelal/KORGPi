package com.github.basshelal.korgpi.mixers

import com.github.basshelal.korgpi.extensions.simpleClassName
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

// For MIDI devices
object MidiMixer {

    fun allMidiDevices(): List<MidiDevice> = MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }

    fun midiOutDevices(): List<MidiDevice> = allMidiDevices().filter { it.simpleClassName == "MidiOutDevice" }

    fun midiInDevices(): List<MidiDevice> = allMidiDevices().filter { it.simpleClassName == "MidiInDevice" }
}