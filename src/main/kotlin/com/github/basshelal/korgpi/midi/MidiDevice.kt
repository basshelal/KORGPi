package com.github.basshelal.korgpi.midi

typealias JavaMidiDevice = javax.sound.midi.MidiDevice

// Wrapper for JavaMidiDevice
class MidiDevice(val javaMidiDevice: JavaMidiDevice) {

    companion object {
        fun fromJavaMidiDevice(javaMidiDevice: JavaMidiDevice): MidiDevice {
            return MidiDevice(javaMidiDevice)
        }
    }
}