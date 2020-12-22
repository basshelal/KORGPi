package com.github.basshelal.korgpi.midi

import com.github.basshelal.korgpi.RealTimeCritical
import com.github.basshelal.korgpi.extensions.getEvent
import com.github.basshelal.korgpi.extensions.midiEventCount
import org.jaudiolibs.jnajack.JackException
import org.jaudiolibs.jnajack.JackMidi
import org.jaudiolibs.jnajack.JackPort

class MidiInPort(val jackPort: JackPort) {

    private val event: JackMidi.Event = JackMidi.Event()
    private val midiMessage: MidiMessage = MidiMessage()
    val receivers: MutableList<MidiReceiver> = mutableListOf()

    @RealTimeCritical
    fun process() {
        try {
            for (i: Int in (0 until jackPort.midiEventCount)) {
                midiMessage.setData(jackPort.getEvent(i, event))
                receivers.forEach { it.onMidiMessage(midiMessage) }
            }
        } catch (ex: JackException) {
            ex.printStackTrace()
        }
    }
}

interface MidiReceiver {

    @RealTimeCritical
    fun onMidiMessage(message: MidiMessage)

    operator fun invoke(message: MidiMessage) = this.onMidiMessage(message)

    companion object {
        inline operator fun invoke(crossinline onMidiMessage: (message: MidiMessage) -> Unit): MidiReceiver =
                object : MidiReceiver {
                    override fun onMidiMessage(message: MidiMessage) = onMidiMessage(message)
                }
    }
}