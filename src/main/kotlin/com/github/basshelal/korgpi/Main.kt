@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi

import com.github.basshelal.korgpi.audio.JavaAudio
import com.github.basshelal.korgpi.extensions.B
import com.github.basshelal.korgpi.extensions.D
import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.extensions.details
import com.github.basshelal.korgpi.extensions.dimensions
import com.github.basshelal.korgpi.extensions.ignoreException
import com.github.basshelal.korgpi.extensions.mixer
import com.github.basshelal.korgpi.log.Log
import com.github.basshelal.korgpi.midi.JavaMidi
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.add
import tornadofx.text
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.sin

// (Bytes per frame * Sample Rate) / 100 (for 10ms latency)

val BUFFER_SIZE = (4 * SAMPLE_RATE) / 100

val outputLine = AudioSystem.getSourceDataLine(EASY_FORMAT).also {
    Log.d(BUFFER_SIZE)
    it.open(it.format, BUFFER_SIZE)
    Log.d(it.bufferSize)
    it.start()
}

val inputLine = AudioSystem.getTargetDataLine(EASY_FORMAT).also {
    it.open(it.format, BUFFER_SIZE)
    Log.d(it.bufferSize)
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
        JavaMidi.allDevices().forEach {
            ignoreException<MidiUnavailableException> {
                it.open()
                it.transmitter.receiver = instrumentReceiver
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

    override fun send(message: MidiMessage, timeStamp: Long) {
        require(message is ShortMessage)
        when (message.command) {
            ShortMessage.NOTE_ON -> {
                GlobalScope.launch {
                    val buffer = sineWave(440, 1, SAMPLE_RATE)
                    outputLine.write(buffer, 0, buffer.size)
                }
            }
            ShortMessage.NOTE_OFF -> {
                GlobalScope.launch { outputLine.flush() }
            }
            ShortMessage.PITCH_BEND -> {
            }
        }
        Log.d(message.details)
    }

    override fun close() {
        Log.d("Closing Receiver")
    }

}

class App : Application() {

    lateinit var synth: Synth

    override fun start(primaryStage: Stage) {
        primaryStage.apply {
            title = "KorgPi"
            dimensions = 250 to 250
            scene = Scene(StackPane().also { stackPane: StackPane ->
                stackPane.add(text("KorgPi").also { it.font = Font.font(45.0) })
            }).also { scene: Scene ->
                scene.setOnKeyPressed { keyEvent: KeyEvent ->
                    Log.d(keyEvent)
                    GlobalScope.launch {
                        val buffer = sineWave(440, 1, SAMPLE_RATE)
                        outputLine.write(buffer, 0, buffer.size)
                    }
                }
                scene.setOnKeyReleased { keyEvent: KeyEvent ->
                    GlobalScope.launch { outputLine.flush() }
                }
            }
            show()
        }
    }

    override fun init() {
        Log.d("Initializing...")
        synth = Synth().create()

        JavaAudio.allDataLines().forEach {
            Log.d("${it.details}\n")
            Log.d("${it.mixer?.details}\n")
        }

        GlobalScope.launch {
            val buffer = ByteArray(BUFFER_SIZE)
            while (true) {
                inputLine.read(buffer, 0, BUFFER_SIZE)
                outputLine.write(buffer, 0, BUFFER_SIZE)
            }
        }
    }

    override fun stop() {
        Log.d("Stopping...")
        synth.destroy()
        outputLine.close()
        System.exit(0)
    }
}

fun sineWave(frequency: Number, seconds: Number, sampleRate: Number): ByteArray {
    val interval = sampleRate.D / frequency.D
    return ByteArray(seconds.I * sampleRate.I) {
        (sin((2.0 * PI * it) / interval) * 127.0).B
    }
}

fun main() {
    Application.launch(App::class.java)
}