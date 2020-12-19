package com.github.basshelal.korgpi.app

import com.github.basshelal.korgpi.audio.AudioOutPort
import com.github.basshelal.korgpi.audio.RealTimeCritical
import com.github.basshelal.korgpi.extensions.D
import com.github.basshelal.korgpi.extensions.F
import com.github.basshelal.korgpi.extensions.forEachIndexed
import com.github.basshelal.korgpi.extensions.set
import com.github.basshelal.korgpi.extensions.zero
import com.github.basshelal.korgpi.jack.MidiInPort
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.midi.MidiMessage
import com.github.basshelal.korgpi.mixers.JackMixer
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.sin

class Synth(midiInPort: MidiInPort, audioOutPort: AudioOutPort) {

    var frequency: Double = 440.0
    var sampleRate = 0.0
    var angle: Double = 0.0
    var angleDelta: Double = 0.0

    var isPressed = false

    init {
        sampleRate = JackMixer.sampleRate.D
        updateAngleDelta()
        logD(angleDelta)
        midiInPort.callbacks.add(this::onMidiMessage)
        audioOutPort.callbacks.add(this::processAudio)
    }

    @RealTimeCritical
    fun onMidiMessage(message: MidiMessage) {
        when (message.command) {
            MidiMessage.NOTE_ON -> {
                isPressed = true
            }
            MidiMessage.NOTE_OFF -> {
                isPressed = false
            }
        }
    }

    @RealTimeCritical
    fun processAudio(floatBuffer: FloatBuffer) {
        if (isPressed) {
            val level = 0.1F
            floatBuffer.forEachIndexed { value, index ->
                val currentSample = sin(angle).F
                angle += angleDelta
                floatBuffer[index] = level * currentSample
            }
        } else {
            floatBuffer.zero()
        }
    }

    fun updateAngleDelta() {
        val cyclesPerSample: Double = frequency / sampleRate
        angleDelta = cyclesPerSample * 2.0 * PI
    }

}