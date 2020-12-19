package com.github.basshelal.korgpi.jack

import com.github.basshelal.korgpi.audio.RealTimeCritical
import com.github.basshelal.korgpi.extensions.getEvent
import com.github.basshelal.korgpi.extensions.midiEventCount
import com.github.basshelal.korgpi.midi.MidiMessage
import org.jaudiolibs.jnajack.JackException
import org.jaudiolibs.jnajack.JackMidi
import org.jaudiolibs.jnajack.JackPort

class MidiInPort(var jackPort: JackPort) {

    private val event: JackMidi.Event = JackMidi.Event()
    private val midiMessage: MidiMessage = MidiMessage()
    val callbacks: MutableList<(MidiMessage) -> Unit> = mutableListOf()

    @RealTimeCritical
    fun process() {
        try {
            for (i: Int in (0 until jackPort.midiEventCount)) {
                midiMessage.setData(jackPort.getEvent(i, event))
                callbacks.forEach {
                    it(midiMessage)
                }
            }
        } catch (ex: JackException) {
            ex.printStackTrace()
        }
    }
}
