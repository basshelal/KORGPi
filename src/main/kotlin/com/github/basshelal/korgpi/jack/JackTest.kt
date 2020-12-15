package com.github.basshelal.korgpi.jack

import org.jaudiolibs.jnajack.Jack
import org.jaudiolibs.jnajack.JackClient
import org.jaudiolibs.jnajack.JackException
import org.jaudiolibs.jnajack.JackMidi
import org.jaudiolibs.jnajack.JackOptions
import org.jaudiolibs.jnajack.JackPort
import org.jaudiolibs.jnajack.JackPortFlags
import org.jaudiolibs.jnajack.JackPortType
import org.jaudiolibs.jnajack.JackProcessCallback
import org.jaudiolibs.jnajack.JackShutdownCallback
import org.jaudiolibs.jnajack.JackStatus
import java.util.EnumSet
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.experimental.and

const val DEBUG = true

class MidiThru : JackProcessCallback, JackShutdownCallback {
    var jackClient: JackClient
    var inputPort: JackPort
    var outputPort: JackPort
    var midiEvent: JackMidi.Event
    var data: ByteArray = ByteArray(0)
    var debugQueue: BlockingQueue<String> = LinkedBlockingQueue()
    var sb: StringBuilder = StringBuilder()

    init {
        val status: EnumSet<JackStatus> = EnumSet.noneOf(JackStatus::class.java)
        try {
            val jack = Jack.getInstance()
            jackClient = jack.openClient("Java MIDI thru test", EnumSet.of(JackOptions.JackNoStartServer), status)
            if (!status.isEmpty()) {
                println("JACK client status : $status")
            }
            inputPort = jackClient.registerPort("MIDI in", JackPortType.MIDI, JackPortFlags.JackPortIsInput)
            outputPort = jackClient.registerPort("MIDI out", JackPortType.MIDI, JackPortFlags.JackPortIsOutput)
            midiEvent = JackMidi.Event()
            if (DEBUG) {
                debugQueue = LinkedBlockingQueue()
                sb = StringBuilder()
            }
        } catch (ex: JackException) {
            if (!status.isEmpty()) {
                println("JACK exception client status : $status")
            }
            throw ex
        }
    }

    fun activate(): MidiThru {
        jackClient.setProcessCallback(this)
        jackClient.onShutdown(this)
        jackClient.activate()
        return this
    }

    override fun process(client: JackClient?, nframes: Int): Boolean {
        return try {
            JackMidi.clearBuffer(outputPort)
            val eventCount = JackMidi.getEventCount(inputPort)
            for (i in 0 until eventCount) {
                JackMidi.eventGet(midiEvent, inputPort, i)
                val size = midiEvent.size()
                if (data.size < size) {
                    data = ByteArray(size)
                }
                midiEvent.read(data)
                if (DEBUG) {
                    sb.setLength(0)
                    sb.append(midiEvent.time())
                    sb.append(": ")
                    for (j in 0 until size) {
                        sb.append(if (j == 0) "" else ", ")
                        sb.append(data[j] and 0xFF.toByte())
                    }
                    debugQueue.offer(sb.toString())
                }
                JackMidi.eventWrite(outputPort, midiEvent.time(), data, midiEvent.size())
            }
            true
        } catch (ex: JackException) {
            println("ERROR : $ex")
            false
        }
    }

    override fun clientShutdown(client: JackClient?) {
        println("Java MIDI thru test shutdown")
    }
}

fun main() {
    try {
        val midiSource = MidiThru().activate()
        while (true) {
            if (DEBUG) {
                val msg = midiSource.debugQueue.take()
                println(msg)
            } else {
                Thread.sleep(100000)
            }
        }
    } catch (ex: Exception) {
        Logger.getLogger(MidiThru::class.java.name).log(Level.SEVERE, null, ex)
    }
}