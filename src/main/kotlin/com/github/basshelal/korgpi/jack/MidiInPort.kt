package com.github.basshelal.korgpi.jack

import com.github.basshelal.korgpi.audio.RealTimeCritical
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.log.logE
import com.github.basshelal.korgpi.mixers.JackMixer
import org.jaudiolibs.jnajack.JackException
import org.jaudiolibs.jnajack.JackMidi
import org.jaudiolibs.jnajack.JackPort

class MidiInPort(var jackPort: JackPort) {

    val event: JackMidi.Event = JackMidi.Event()
    var buffer: ByteArray = ByteArray(0)
    val callbacks: MutableList<(ByteArray) -> Unit> = mutableListOf()

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
                callbacks.forEach {
                    it(buffer)
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
            it.forEach { logD(it) }
            logD("-----------------")
        }
        JackMixer.jackClient.setProcessCallback { client, nframes ->
            try {
                midiInPort.process()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
        JackMixer.jackClient.activate()
        JackMixer.jackInstance.connect(JackMixer.jackClient, "a2j:microKEY-25 [20] (capture): microKEY-25 MIDI 1", "KorgPi:MIDI In Port")
        Thread.sleep(Long.MAX_VALUE)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}