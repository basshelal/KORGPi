package com.github.basshelal.korgpi.app

import com.github.basshelal.korgpi.APP_NAME
import com.github.basshelal.korgpi.audio.Synth
import com.github.basshelal.korgpi.extensions.addOnSystemShutdownCallback
import com.github.basshelal.korgpi.extensions.dimensions
import com.github.basshelal.korgpi.log.logD
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
import tornadofx.add
import tornadofx.text

class App : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.apply {
            title = APP_NAME
            dimensions = 250 to 250
            scene = Scene(StackPane().also { stackPane: StackPane ->
                stackPane.add(text(APP_NAME).also { it.font = Font.font(45.0) })
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
        JackMixer.jackInstance.connect(JackMixer.jackClient, "a2j:microKEY-25 [28] (capture): microKEY-25 MIDI 1", midiInPort.jackPort.name)
        JackMixer.jackInstance.connect(JackMixer.jackClient, audioOutPort.jackPort.name, "system:playback_1")
        JackMixer.jackInstance.connect(JackMixer.jackClient, audioOutPort.jackPort.name, "system:playback_2")
        addOnSystemShutdownCallback { JackMixer.jackClient.deactivate() }
        Thread.sleep(Long.MAX_VALUE)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}