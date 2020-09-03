@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi

import javafx.application.Application
import javafx.stage.Stage
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin

class Synth {

    val instrumentReceiver = InstrumentReceiver()

    fun create(): Synth {
        KorgPi.allMixers().forEach { mixer ->
            println(mixer.info)
            mixer.allLines().forEach { line ->
                if (line is SourceDataLine) {
                    println("Line ${line}")
                    println(line.lineInfo)
                    line.open()
                    line.start()
                    val buffer = sineWave(440, 3, SAMPLE_RATE)
                    line.write(buffer, 0, buffer.size)
                    line.close()
                }
            }
        }
        startMidi()
        return this
    }

    fun destroy(): Synth {
        stopMidi()
        return this
    }

    private fun startMidi() {
        MidiSystem.getMidiDeviceInfo().forEach {
            ignoreException<MidiUnavailableException> {
                MidiSystem.getMidiDevice(it).also {
                    it.transmitter.receiver = instrumentReceiver
                    it.open()
                    println("${it.deviceInfo} was opened")
                }
            }
        }
    }

    private fun stopMidi() {
        MidiSystem.getMidiDeviceInfo().forEach {
            MidiSystem.getMidiDevice(it).also {
                if (it.isOpen) it.close()
                println("${it} is closed")
            }
        }
    }
}

class InstrumentReceiver : Receiver {

    val line = AudioSystem.getSourceDataLine(EASY_FORMAT).also {
        it.open(it.format)
        it.start()
    }

    override fun send(message: MidiMessage, timeStamp: Long) {
        require(message is ShortMessage)
        when (message.command) {
            ShortMessage.NOTE_ON -> {
                thread {
                    val buffer = sineWave(440, 1, SAMPLE_RATE)
                    line.write(buffer, 0, buffer.size)
                }
                println("Note on")
                println("TimeStamp: $timeStamp")
                println("Channel: ${message.channel}")
                println("Command: ${message.command}")
                println("Data1 (Note): ${message.data1}") // Note value
                println("Data2 (Vel) : ${message.data2}") // Velocity
                println()
            }
            ShortMessage.NOTE_OFF -> {
                thread {
                    line.flush()
                }
                println("Note off")
                println()
            }
            ShortMessage.PITCH_BEND -> {
                println("Pitch Bend")
                println("Command: ${message.command}")
                println("Data1: ${message.data1}") // Note value
                println("Data2: ${message.data2}") // Velocity
                println()
            }
        }
        println("TimeStamp: $timeStamp")
        println("Channel: ${message.channel}")
        println("Command: ${message.command}")
        println("Data1: ${message.data1}") // Note value
        println("Data2: ${message.data2}") // Velocity
        println()
    }

    override fun close() {
        println("Closing Receiver")
    }

}

class App : Application() {

    lateinit var synth: Synth

    override fun start(primaryStage: Stage) {
        primaryStage.title = "App"
        primaryStage.width = 100.0
        primaryStage.height = 100.0
        primaryStage.show()
    }

    override fun init() {
        println("Initializing...")
        synth = Synth().create()
    }

    override fun stop() {
        println("Stopping...")
        synth.destroy()
        System.exit(0)
    }
}

fun sineWave(frequency: Int, seconds: Int, sampleRate: Int): ByteArray {
    val samples = seconds * sampleRate
    val result = ByteArray(samples)
    val interval = sampleRate.toDouble() / frequency
    for (i in 0 until samples) {
        val angle = 2.0 * PI * i / interval
        result[i] = (sin(angle) * 127).toInt().toByte()
    }
    return result
}

fun main() {
    Application.launch(App::class.java)
}