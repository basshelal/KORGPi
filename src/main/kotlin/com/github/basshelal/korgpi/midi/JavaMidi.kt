@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.midi

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver

object JavaMidi {
    inline fun allDevices(): List<MidiDevice> {
        return MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }
    }
}

abstract class SimpleReceiver : Receiver {
    override fun close() {}
    override fun send(message: MidiMessage, timeStamp: Long) {}

    companion object {
        inline operator fun invoke(
            crossinline close: () -> Unit = {},
            crossinline send: (message: MidiMessage, timeStamp: Long) -> Unit = { message, timeStamp -> }
        ): SimpleReceiver {
            return object : SimpleReceiver() {
                override fun close() = close()
                override fun send(message: MidiMessage, timeStamp: Long) = send(message, timeStamp)
            }
        }
    }
}