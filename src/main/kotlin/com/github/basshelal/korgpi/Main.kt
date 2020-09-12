@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi

import com.github.basshelal.korgpi.log.Log
import com.github.basshelal.korgpi.midi.JavaMidi
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.sampled.AudioSystem
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin

val line = AudioSystem.getSourceDataLine(EASY_FORMAT).also {
    it.open(it.format)
    it.start()
}

class Synth {

    val instrumentReceiver = InstrumentReceiver()

    fun create(): Synth {
        startMidi()
        return this
    }

    fun destroy(): Synth {
        stopMidi()
        return this
    }

    private fun startMidi() {
        try {
            // TODO: 12-Sep-20 Below wont work when using the Focusrite because JavaSound
            //  picks it as the default MIDI instead of the USB MIDI, so we need to unplug the Focusrite
            //  before playing, this sucks so we need to figure out a way to allow us to pick the MIDI in Transmitter
            MidiSystem.getTransmitter().receiver = instrumentReceiver
        } catch (e: Exception) {
            e.printStackTrace()
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

    override fun send(message: MidiMessage, timeStamp: Long) {
        require(message is ShortMessage)
        when (message.command) {
            ShortMessage.NOTE_ON -> {
                thread {
                    val buffer = sineWave(440, 1, SAMPLE_RATE)
                    line.write(buffer, 0, buffer.size)
                }
            }
            ShortMessage.NOTE_OFF -> {
                thread { line.flush() }
            }
            ShortMessage.PITCH_BEND -> {
            }
        }
        Log.d(message.info)
    }

    override fun close() {
        Log.d("Closing Receiver")
    }

}

class App : Application() {

    lateinit var synth: Synth

    override fun start(primaryStage: Stage) {
        primaryStage.apply {
            title = "App"
            dimensions = 250 to 250
            scene = Scene(StackPane()).also {
                it.setOnKeyPressed { keyEvent: KeyEvent ->
                    Log.d(keyEvent)
                    thread {
                        val buffer = sineWave(440, 1, SAMPLE_RATE)
                        line.write(buffer, 0, buffer.size)
                    }
                }
                it.setOnKeyReleased { keyEvent: KeyEvent ->
                    thread { line.flush() }
                }
            }
            show()
        }
    }

    override fun init() {
        Log.d("Initializing...")
        synth = Synth().create()
    }

    override fun stop() {
        Log.d("Stopping...")
        synth.destroy()
        line.close()
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