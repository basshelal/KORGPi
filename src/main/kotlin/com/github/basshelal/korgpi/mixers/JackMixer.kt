package com.github.basshelal.korgpi.mixers

import com.github.basshelal.korgpi.APP_NAME
import com.github.basshelal.korgpi.audio.JackAudioOutPort
import com.github.basshelal.korgpi.extensions.openClient
import com.github.basshelal.korgpi.log.logE
import com.github.basshelal.korgpi.midi.JackMidiInPort
import org.jaudiolibs.jnajack.Jack
import org.jaudiolibs.jnajack.JackClient
import org.jaudiolibs.jnajack.JackException
import org.jaudiolibs.jnajack.JackPort
import org.jaudiolibs.jnajack.JackPortFlags
import org.jaudiolibs.jnajack.JackPortType
import org.jaudiolibs.jnajack.JackProcessCallback

object JackMixer {

    lateinit var jackInstance: Jack
    lateinit var jackClient: JackClient
    var sampleRate: Int = 0

    fun initialize() {
        if (!this::jackInstance.isInitialized) {
            jackInstance = Jack.getInstance()
        }
        if (!this::jackClient.isInitialized) {
            jackClient = jackInstance.openClient(APP_NAME)
            sampleRate = jackClient.sampleRate
        }
    }

    fun start(processCallback: JackProcessCallback) {
        jackClient.setProcessCallback(processCallback)
        jackClient.activate()
    }

    object Midi {

        private val _inPorts = mutableListOf<JackMidiInPort>()
        val inPorts: List<JackMidiInPort> get() = _inPorts.toList()

        @Throws(JackException::class)
        fun getMidiInPort(name: String): JackMidiInPort {
            return _inPorts.find { it.jackPort.shortName == name } ?: try {
                val jackPort: JackPort = jackClient.registerPort(name, JackPortType.MIDI, JackPortFlags.JackPortIsInput)
                val midiInPort = JackMidiInPort(jackPort)
                _inPorts.add(midiInPort)
                return midiInPort
            } catch (je: JackException) {
                logE("JackException while trying to get MIDI in port with name: $name")
                je.printStackTrace()
                throw je
            }
        }

        @Throws(JackException::class)
        fun removeMidiInPort(name: String) {
            val found: JackMidiInPort? = _inPorts.find { it.jackPort.shortName == name }
            if (found !== null) {
                _inPorts.remove(found)
                try {
                    jackClient.unregisterPort(found.jackPort)
                } catch (je: JackException) {
                    logE("JackException while trying to remove MIDI in port with name: $name")
                    je.printStackTrace()
                    throw je
                }
            }
        }

    }

    object Audio {

        private val _outPorts = mutableListOf<JackAudioOutPort>()
        val outPorts: List<JackAudioOutPort> = _outPorts.toList()

        @Throws(JackException::class)
        fun getAudioAudioPort(name: String): JackAudioOutPort {
            return _outPorts.find { it.jackPort.shortName == name } ?: try {
                val jackPort: JackPort = jackClient.registerPort(name, JackPortType.AUDIO, JackPortFlags.JackPortIsOutput)
                val audioOutPort = JackAudioOutPort(jackPort)
                _outPorts.add(audioOutPort)
                return audioOutPort
            } catch (je: JackException) {
                logE("JackException while trying to get Audio out port with name: $name")
                je.printStackTrace()
                throw je
            }
        }

        @Throws(JackException::class)
        fun removeAudioOutPort(name: String) {
            val found: JackAudioOutPort? = _outPorts.find { it.jackPort.shortName == name }
            if (found !== null) {
                _outPorts.remove(found)
                try {
                    jackClient.unregisterPort(found.jackPort)
                } catch (je: JackException) {
                    logE("JackException while trying to remove Audio out port with name: $name")
                    je.printStackTrace()
                    throw je
                }
            }
        }

    }

}