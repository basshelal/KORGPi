package com.github.basshelal.korgpi.app

import com.github.basshelal.korgpi.JackMixer
import com.github.basshelal.korgpi.extensions.addOnSystemShutdownCallback
import com.github.basshelal.korgpi.extensions.dimensions
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.log.logE
import com.github.basshelal.korgpi.midi.MidiMessage
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
import tornadofx.add
import tornadofx.text

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
    }

    override fun stop() {
        logD("Stopping...")
        System.exit(0)
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
        val synth = Synth(midiInPort, audioOutPort)
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
        JackMixer.jackInstance.connect(JackMixer.jackClient, "KorgPi:Audio Out Port", "system:playback_1")
        JackMixer.jackInstance.connect(JackMixer.jackClient, "KorgPi:Audio Out Port", "system:playback_2")
        addOnSystemShutdownCallback { JackMixer.jackClient.deactivate() }
        Thread.sleep(Long.MAX_VALUE)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}