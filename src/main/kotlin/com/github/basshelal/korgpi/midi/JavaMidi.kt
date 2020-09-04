@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.midi

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

object JavaMidi {
    inline fun allDevices(): List<MidiDevice> {
        return MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }
    }
}