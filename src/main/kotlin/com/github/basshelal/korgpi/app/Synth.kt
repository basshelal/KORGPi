package com.github.basshelal.korgpi.app

import com.github.basshelal.korgpi.audio.AudioOutPort
import com.github.basshelal.korgpi.extensions.forEachIndexed
import com.github.basshelal.korgpi.extensions.set
import com.github.basshelal.korgpi.jack.MidiInPort
import com.github.basshelal.korgpi.midi.MidiMessage
import java.nio.FloatBuffer
import kotlin.random.Random

class Synth(midiInPort: MidiInPort, audioOutPort: AudioOutPort) {

    var isPressed = false

    init {
        midiInPort.callbacks.add { message: MidiMessage ->
            when (message.command) {
                MidiMessage.NOTE_ON -> isPressed = true
                MidiMessage.NOTE_OFF -> isPressed = false
            }
        }

        audioOutPort.callbacks.add { floatBuffer: FloatBuffer ->
            if (isPressed) {
                floatBuffer.forEachIndexed { value, index ->
                    floatBuffer[index] = 0.1F * Random.nextFloat()
                }
            } else {
                floatBuffer.forEachIndexed { value, index ->
                    floatBuffer[index] = 0.0F
                }
            }
            //floatBuffer.forEachIndexed { value, index -> logD(value) }
        }

    }


}