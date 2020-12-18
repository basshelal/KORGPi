package com.github.basshelal.korgpi.jack

import com.github.basshelal.korgpi.audio.RealTimeCritical
import com.github.basshelal.korgpi.extensions.addOnSystemShutdownCallback
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.log.logE
import com.github.basshelal.korgpi.midi.MidiMessage
import com.github.basshelal.korgpi.mixers.JackMixer
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

fun main() {
    try {
        JackMixer.initialize()
        val port: JackPort = JackMixer.Midi.getMidiInPort("MIDI In Port")
        val midiInPort = MidiInPort(port)
        midiInPort.callbacks.add {
            when (it.command) {
                MidiMessage.NOTE_ON -> logE("NOTE ON")
                MidiMessage.NOTE_OFF -> logE("NOTE OFF")
                MidiMessage.PITCH_BEND -> logE("PITCH BEND")
                MidiMessage.CONTROL_CHANGE -> logE("CONTROL CHANGE")
            }
            logD("cmmnd: ${it.command}")
            logD("data1: ${it.data1}")
            logD("data2: ${it.data2}")
            logD("-----------------")
        }
        JackMixer.start { client, nframes ->
            try {
                midiInPort.process()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
        JackMixer.jackInstance.connect(JackMixer.jackClient, "a2j:microKEY-25 [20] (capture): microKEY-25 MIDI 1", "KorgPi:MIDI In Port")
        addOnSystemShutdownCallback { JackMixer.jackClient.deactivate() }
        Thread.sleep(Long.MAX_VALUE)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}