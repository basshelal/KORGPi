@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.app

import com.github.basshelal.korgpi.DEFAULT_FORMAT
import com.github.basshelal.korgpi.Key
import com.github.basshelal.korgpi.SAMPLE_RATE
import com.github.basshelal.korgpi.audio.JavaAudio
import com.github.basshelal.korgpi.audio.ReadWriteLineThread
import com.github.basshelal.korgpi.audio.ReadableLine
import com.github.basshelal.korgpi.audio.WritableLine
import com.github.basshelal.korgpi.extensions.B
import com.github.basshelal.korgpi.extensions.D
import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.extensions.details
import com.github.basshelal.korgpi.extensions.dimensions
import com.github.basshelal.korgpi.extensions.ignoreException
import com.github.basshelal.korgpi.extensions.mixer
import com.github.basshelal.korgpi.extensions.now
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.midi.JavaMidi
import com.github.basshelal.korgpi.midi.SimpleReceiver
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornadofx.add
import tornadofx.text
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.ShortMessage
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.sin

// (Bytes per frame * Sample Rate) / 100 (for 10ms latency)

val BUFFER_SIZE = ((2 * SAMPLE_RATE) / 10F).I

val outputLine = AudioSystem.getSourceDataLine(DEFAULT_FORMAT).also {
    it.open(it.format, BUFFER_SIZE)
    it.start()
}

val inputLine = AudioSystem.getTargetDataLine(DEFAULT_FORMAT).also {
    it.open(it.format, BUFFER_SIZE)
    it.start()
}

class Synth {

    val instrumentReceiver = SimpleReceiver { message: MidiMessage, timeStamp: Long ->
        require(message is ShortMessage)
        when (message.command) {
            ShortMessage.NOTE_ON -> {
                GlobalScope.launch {
                    val buffer = playNote(message.data1)
                    outputLine.write(buffer, 0, buffer.size)
                }
            }
            ShortMessage.NOTE_OFF -> {
                GlobalScope.launch { outputLine.flush() }
            }
            ShortMessage.PITCH_BEND -> {
            }
        }
        logD(message.details)
    }

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
        }
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
                stackPane.background = Background(BackgroundFill(Color.DARKGREY, CornerRadii(1.0),
                        Insets(0.0, 0.0, 0.0, 0.0)))
            }).also { scene: Scene ->
                scene.setOnKeyPressed { keyEvent: KeyEvent ->
                    logD(keyEvent)
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
        logD("Initializing...")
        synth = Synth().create()

        JavaAudio.allDataLines().forEach {
            logD("${it.details}\n")
            logD("${it.mixer?.details}\n")
        }

        GlobalScope.launch {
            val buffer = ByteArray(BUFFER_SIZE)
            while (true) {
                //    inputLine.read(buffer, 0, BUFFER_SIZE)
                //    outputLine.write(buffer, 0, BUFFER_SIZE)
            }
        }

        val buffer = ByteArray(BUFFER_SIZE)
        val readLine = ReadableLine(inputLine)
        val writeLine = WritableLine(outputLine)
        val thread = ReadWriteLineThread(readLine, writeLine, buffer)
        thread.start()

        GlobalScope.launch {
            delay(5000)
            logD(thread.isAlive)
            thread.running = false
            logD("Killed at $now")
            delay(1000)
            logD(thread.isAlive)
        }

    }

    override fun stop() {
        logD("Stopping...")
        synth.destroy()
        outputLine.close()
        System.exit(0)
    }
}

fun sineWave(frequency: Number, seconds: Number, sampleRate: Number = SAMPLE_RATE): ByteArray {
    val interval = sampleRate.D / frequency.D
    return ByteArray(seconds.I * sampleRate.I) {
        (sin((2.0 * PI * it) / interval) * 127.0).B
    }
}

fun playNote(noteNumber: Int): ByteArray {
    val noteFrequency: Double = Key.fromNumber(noteNumber).frequency
    val interval = SAMPLE_RATE.D / noteFrequency
    return ByteArray(SAMPLE_RATE.I) {
        (sin((2.0 * PI * it) / interval) * 127.0).B
    }
}

fun main() {
    Application.launch(App::class.java)
}