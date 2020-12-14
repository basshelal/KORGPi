package com.github.basshelal.korgpi.midi

typealias JMidiDevice = javax.sound.midi.MidiDevice

// Wrapper for JavaMidiDevice
open class MidiDevice(val jMidiDevice: JMidiDevice) {

    companion object {
        fun fromJavaMidiDevice(jMidiDevice: JMidiDevice): MidiDevice {
            return MidiDevice(jMidiDevice)
        }
    }
}