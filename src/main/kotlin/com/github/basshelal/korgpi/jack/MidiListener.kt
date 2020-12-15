package com.github.basshelal.korgpi.jack

import com.github.basshelal.korgpi.audio.RealTimeCritical
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.log.logE
import org.jaudiolibs.jnajack.Jack
import org.jaudiolibs.jnajack.JackClient
import org.jaudiolibs.jnajack.JackException
import org.jaudiolibs.jnajack.JackMidi
import org.jaudiolibs.jnajack.JackOptions
import org.jaudiolibs.jnajack.JackPort
import org.jaudiolibs.jnajack.JackPortFlags
import org.jaudiolibs.jnajack.JackPortType
import org.jaudiolibs.jnajack.JackProcessCallback
import org.jaudiolibs.jnajack.JackStatus
import java.util.EnumSet
import java.util.logging.Level
import java.util.logging.Logger

class MidiListener : JackProcessCallback {
    val jackInstance: Jack = Jack.getInstance()
    var jackClient: JackClient
    var inputPort: JackPort
    var outputPort: JackPort
    var midiEvent: JackMidi.Event
    var data: ByteArray = ByteArray(0)

    init {
        val status: EnumSet<JackStatus> = EnumSet.noneOf(JackStatus::class.java)
        try {
            jackClient = jackInstance.openClient("Java MIDI thru test", EnumSet.of(JackOptions.JackNoStartServer), status)
            if (!status.isEmpty()) {
                logD("JACK client status : $status")
            }
            inputPort = jackClient.registerPort("MIDI in", JackPortType.MIDI, JackPortFlags.JackPortIsInput)
            outputPort = jackClient.registerPort("Audio out", JackPortType.AUDIO, JackPortFlags.JackPortIsOutput)
            midiEvent = JackMidi.Event()
        } catch (ex: JackException) {
            if (!status.isEmpty()) {
                logE("JACK exception client status : $status")
            }
            throw ex
        }
    }

    fun activate(): MidiListener {
        jackClient.setProcessCallback(this)
        jackClient.onShutdown { logD("Shutting down Jack Client") }
        jackClient.activate()
        return this
    }

    @RealTimeCritical
    override fun process(client: JackClient, nframes: Int): Boolean {
        outputPort.buffer.clear()
        return try {
            for (i in 0 until JackMidi.getEventCount(inputPort)) {
                JackMidi.eventGet(midiEvent, inputPort, i)
                val size = midiEvent.size()
                if (data.size < size) {
                    data = ByteArray(size)
                }
                midiEvent.read(data)

                outputPort.floatBuffer.put(0.5F)

                if (DEBUG) {
                    logD(midiEvent.time())
                    logD(data)
                }
            }
            true
        } catch (ex: JackException) {
            logE("ERROR : $ex")
            false
        }
    }

}

fun main() {
    try {
        val midiSource = MidiListener().activate()
        Thread.sleep(Long.MAX_VALUE)
    } catch (ex: Exception) {
        Logger.getLogger(MidiThru::class.java.name).log(Level.SEVERE, null, ex)
    }
}