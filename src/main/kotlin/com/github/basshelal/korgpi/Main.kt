@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi

import com.github.basshelal.korgpi.log.Log
import com.github.basshelal.korgpi.midi.JavaMidi
import com.github.basshelal.korgpi.sound.JavaSound
import javafx.application.Application
import javafx.stage.Stage
import javax.sound.midi.MidiMessage
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
        JavaSound.allMixers().forEach { mixer ->
            Log.d(mixer.info)
            mixer.allLines().forEach { line ->
                if (line is SourceDataLine) {
                    Log.d("Line ${line}")
                    Log.d(line.lineInfo)
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
        ignoreException<MidiUnavailableException> {
            JavaMidi.allDevices().forEach {
                it.transmitter.receiver = instrumentReceiver
                it.open()
                Log.d("${it.deviceInfo} was opened")
            }
        }
    }

    private fun stopMidi() {
        JavaMidi.allDevices().forEach {
            if (it.isOpen) it.close()
            Log.d("${it} is closed")
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
                Log.d("Note on")
                Log.d("TimeStamp: $timeStamp")
                Log.d("Channel: ${message.channel}")
                Log.d("Command: ${message.command}")
                Log.d("Data1 (Note): ${message.data1}") // Note value
                Log.d("Data2 (Vel) : ${message.data2}") // Velocity
                Log.d()
            }
            ShortMessage.NOTE_OFF -> {
                thread {
                    line.flush()
                }
                Log.d("Note off")
                Log.d()
            }
            ShortMessage.PITCH_BEND -> {
                Log.d("Pitch Bend")
                Log.d("Command: ${message.command}")
                Log.d("Data1: ${message.data1}") // Note value
                Log.d("Data2: ${message.data2}") // Velocity
                Log.d()
            }
        }
        Log.d("TimeStamp: $timeStamp")
        Log.d("Channel: ${message.channel}")
        Log.d("Command: ${message.command}")
        Log.d("Data1: ${message.data1}") // Note value
        Log.d("Data2: ${message.data2}") // Velocity
        Log.d()
    }

    override fun close() {
        Log.d("Closing Receiver")
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
        Log.d("Initializing...")
        synth = Synth().create()
    }

    override fun stop() {
        Log.d("Stopping...")
        synth.destroy()
        System.exit(0)
    }
}

fun sineWave(frequency: Int, seconds: Int, sampleRate: Int): ByteArray {
    val interval = sampleRate.D / frequency.D
    return ByteArray(seconds * sampleRate) {
        (sin((2.0 * PI * it) / interval) * 127.0).B
    }
}

fun main() {
    Application.launch(App::class.java)
}