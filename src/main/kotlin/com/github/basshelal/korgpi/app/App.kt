package com.github.basshelal.korgpi.app

import com.github.basshelal.korgpi.Key
import com.github.basshelal.korgpi.audio.Formats
import com.github.basshelal.korgpi.audio.ReadWriteLineThread
import com.github.basshelal.korgpi.audio.ReadableLine
import com.github.basshelal.korgpi.audio.SAMPLE_RATE
import com.github.basshelal.korgpi.audio.WritableLine
import com.github.basshelal.korgpi.extensions.B
import com.github.basshelal.korgpi.extensions.D
import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.extensions.addOnSystemShutdownCallback
import com.github.basshelal.korgpi.extensions.dimensions
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.log.logE
import com.github.basshelal.korgpi.midi.MidiMessage
import com.github.basshelal.korgpi.mixers.JackMixer
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
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
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.sin

// Entry point is specifying Audio Format because with format we can get Lines, and with Lines we can open we can
// then access Audio Devices etc etc. So before anything, we need to determine Audio Format!

val BUFFER_SIZE = ((2 * SAMPLE_RATE) / 100F).I

val outputLine = AudioSystem.getSourceDataLine(Formats.default).also {
    it.open(Formats.default, BUFFER_SIZE)
    it.start()
}

val inputLine = AudioSystem.getTargetDataLine(Formats.default).also {
    it.open(Formats.default, BUFFER_SIZE)
    it.start()
}

class App : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.apply {
            title = "KorgPi"
            dimensions = 250 to 250
            scene = Scene(StackPane().also { stackPane: StackPane ->
                stackPane.add(text("KorgPi").also { it.font = Font.font(45.0) })
                stackPane.background = Background(BackgroundFill(Color.web("#424242"), CornerRadii(1.0),
                        Insets(0.0, 0.0, 0.0, 0.0)))
            })
        }.show()
    }

    override fun init() {
        logD("Initializing...")

        val buffer = ByteArray(BUFFER_SIZE)
        val readLine = ReadableLine(inputLine)
        val writeLine = WritableLine(outputLine)
        val thread = ReadWriteLineThread(readLine, writeLine, buffer)
        thread.start()

        GlobalScope.launch {
            delay(5000)
            thread.kill()
        }

    }

    override fun stop() {
        logD("Stopping...")
        System.exit(0)
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
    // logD("Launching Application")
    // Application.launch(App::class.java)
    try {
        JackMixer.initialize()
        val midiInPort = JackMixer.Midi.getMidiInPort("MIDI In Port")
        val audioOutPort = JackMixer.Audio.getAudioAudioPort("Audio Out Port")
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
                audioOutPort.process()
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