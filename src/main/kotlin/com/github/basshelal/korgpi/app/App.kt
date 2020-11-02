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
import com.github.basshelal.korgpi.extensions.details
import com.github.basshelal.korgpi.extensions.dimensions
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.mixers.AudioMixer
import com.github.basshelal.korgpi.mixers.MidiMixer
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
    logD("All Audio Devices")
    AudioMixer.allAudioDevices().forEach {
        logD("${it.details}\n")
        logD("Writable Lines:")
        it.jMixer.sourceLineInfo.forEach { logD("\t$it") }
        logD("Readable Lines:")
        it.jMixer.targetLineInfo.forEach { logD("\t$it") }
    }
    logD("All Usable Audio Devices")
    AudioMixer.allUsableAudioDevices().forEach { logD("${it.details}\n") }
    logD("All Readable Data Lines")
    AudioMixer.allReadableDataLines().forEach { logD("${it.details}\n") }
    logD("All Writeable Data Lines")
    AudioMixer.allWriteableDataLines().forEach { logD("${it.details}\n") }
    logD("Midi In Devices")
    MidiMixer.midiInDevices().forEach { logD("${it.details}\n") }
    logD("Midi Out Devices")
    MidiMixer.midiOutDevices().forEach { logD("${it.details}\n") }
    logD("Launching Application")
    // Application.launch(App::class.java)
}