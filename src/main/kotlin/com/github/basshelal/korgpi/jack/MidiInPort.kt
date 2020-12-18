package com.github.basshelal.korgpi.jack

import com.github.basshelal.korgpi.audio.RealTimeCritical
import com.github.basshelal.korgpi.log.logE
import com.github.basshelal.korgpi.midi.MidiMessage
import org.jaudiolibs.jnajack.JackException
import org.jaudiolibs.jnajack.JackMidi
import org.jaudiolibs.jnajack.JackPort

class MidiInPort(var jackPort: JackPort) {

    val event: JackMidi.Event = JackMidi.Event()
    var buffer: ByteArray = ByteArray(0)
    val midiMessage: MidiMessage = MidiMessage()
    val callbacks: MutableList<(MidiMessage) -> Unit> = mutableListOf()

    @RealTimeCritical
    fun process() {
        val eventCount: Int = JackMidi.getEventCount(jackPort)
        try {
            for (i: Int in (0 until eventCount)) {
                JackMidi.eventGet(event, jackPort, i)
                val size = event.size()
                if (buffer.size < size) {
                    buffer = ByteArray(size)
                }
                event.read(buffer)
                midiMessage.setData(buffer)
                // TODO: 18/12/2020 Calling callbacks might need to be asynchronous to prevent blocking
                callbacks.forEach {
                    it(midiMessage)
                }
            }
        } catch (ex: JackException) {
            logE("ERROR : $ex")
        }
    }
}
