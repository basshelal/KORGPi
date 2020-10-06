@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.core

import com.github.basshelal.korgpi.extensions.allLines
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer

// TODO: 06/10/2020 GitHub Issue #1
// The Global Mixer instance for the entire app
object AppMixer {

    // List ALL devices
    //  In these there are audio devices (those with lines)
    //  And MIDI devices (those with receivers and transmitters)

    // Filter between IN devices and OUT devices

    // Connect devices to each other

    inline fun allMixers(): List<Mixer> = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }

    inline fun allLines(): List<Line> = allMixers().flatMap { it.allLines() }

    inline fun allDataLines(): List<DataLine> = allLines().filterIsInstance<DataLine>()

    inline fun allMidiDevices(): List<MidiDevice> = MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }

}