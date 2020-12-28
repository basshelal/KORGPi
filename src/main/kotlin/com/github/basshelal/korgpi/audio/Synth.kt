package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.JackMixer
import com.github.basshelal.korgpi.Key
import com.github.basshelal.korgpi.Note
import com.github.basshelal.korgpi.RealTimeCritical
import com.github.basshelal.korgpi.TWOPI
import com.github.basshelal.korgpi.extensions.D
import com.github.basshelal.korgpi.extensions.F
import com.github.basshelal.korgpi.extensions.convertScale
import com.github.basshelal.korgpi.extensions.updateEach
import com.github.basshelal.korgpi.extensions.zero
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.midi.MidiInPort
import com.github.basshelal.korgpi.midi.MidiMessage
import com.github.basshelal.korgpi.midi.MidiReceiver
import java.nio.FloatBuffer
import kotlin.math.sin

class Synth(midiInPort: MidiInPort, audioOutPort: AudioOutPort)
    : MidiReceiver, AudioProcessor {

    private val voices: List<SynthVoice>
    private var centsModifier: Int = 0

    private inline val activeVoices: List<SynthVoice>
        get() = voices.filter { it.isActive }

    init {
        val sampleRate = JackMixer.sampleRate.D
        val bufferSize = JackMixer.jackClient.bufferSize
        midiInPort.receivers.add(this)
        audioOutPort.audioProcessors.add(this)
        voices = List(MidiMessage.MAX_NOTES) {
            SynthVoice(sampleRate, bufferSize)
        }
    }

    @RealTimeCritical
    override fun onMidiMessage(message: MidiMessage) {
        val key = Key.fromMidiNumber(message.data1.toInt())
        when (message.command) {
            MidiMessage.NOTE_ON -> {
                val voice = voices.find { !it.isActive }
                centsModifier = convertScale(MidiMessage.PITCH_BEND_MIN, MidiMessage.PITCH_BEND_MAX,
                        -100, 100, message.pitchBendValueRaw)
                if (key.note == Note.E || key.note == Note.B) { // Maqam Huseyni/Ussak on D
                    voice?.centsModifier = -50
                }
                voice?.activate(key)
            }
            MidiMessage.NOTE_OFF -> {
                val voice = voices.find { it.isActive && it.key == key }
                voice?.centsModifier = 0
                voice?.deactivate()
            }
            MidiMessage.PITCH_BEND -> {
                centsModifier = convertScale(MidiMessage.PITCH_BEND_MIN, MidiMessage.PITCH_BEND_MAX,
                        -100, 100, message.pitchBendValueRaw)
                logD(centsModifier)
                activeVoices.forEach {
                    it.centsModifier = centsModifier
                    it.updateAngleDelta()
                }
            }
        }
    }

    @RealTimeCritical
    override fun processAudio(buffer: FloatBuffer) {
        val activeVoices = voices.filter { it.isActive }
        //  if (activeVoices.isNotEmpty()) logD(activeVoices.map { it.key })
        if (activeVoices.isNotEmpty())
            activeVoices.forEachIndexed { voiceIndex: Int, voice: SynthVoice ->
                voice.processAudio()
                if (voiceIndex == 0) {
                    buffer.updateEach { value, index ->
                        return@updateEach voice.buffer[index]
                    }
                } else {
                    buffer.updateEach { value, index ->
                        return@updateEach value + voice.buffer[index]
                    }
                }
            }
        else buffer.zero()
    }

}

class SynthVoice(private var sampleRate: Double, bufferSize: Int) {
    private var angle: Double = 0.0
    private var angleDelta: Double = 0.0

    var key: Key = Key.NULL
    var level = 0.05F
    var isActive: Boolean = false
    var centsModifier: Int = 0

    val buffer: FloatBuffer = FloatBuffer.allocate(bufferSize)

    @RealTimeCritical
    fun processAudio(): FloatBuffer {
        if (isActive && key != Key.NULL) {
            buffer.updateEach { value, index ->
                val currentSample = sin(angle).F
                angle += angleDelta
                return@updateEach level * currentSample
            }
        } else {
            buffer.zero()
        }
        return buffer
    }

    fun activate(key: Key) {
        this.key = key
        this.isActive = true
        updateAngleDelta()
    }

    fun deactivate() {
        this.key = Key.NULL
        this.isActive = false
    }

    fun updateAngleDelta() {
        val cyclesPerSample: Double = (key addCents centsModifier) / sampleRate
        angleDelta = cyclesPerSample * TWOPI
    }

}