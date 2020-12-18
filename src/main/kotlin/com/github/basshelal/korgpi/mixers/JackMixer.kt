package com.github.basshelal.korgpi.mixers

import com.github.basshelal.korgpi.audio.AudioOutPort
import com.github.basshelal.korgpi.extensions.openClient
import com.github.basshelal.korgpi.jack.MidiInPort
import com.github.basshelal.korgpi.log.logE
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

    fun initialize() {
        if (!this::jackInstance.isInitialized) {
            jackInstance = Jack.getInstance()
        }
        if (!this::jackClient.isInitialized) {
            jackClient = jackInstance.openClient("KorgPi")
        }
    }

    fun start(processCallback: JackProcessCallback) {
        jackClient.setProcessCallback(processCallback)
        jackClient.activate()
    }

    object Midi {

        private val _inPorts = mutableListOf<MidiInPort>()
        val inPorts: List<MidiInPort> get() = _inPorts.toList()

        @Throws(JackException::class)
        fun getMidiInPort(name: String): MidiInPort {
            return this._inPorts.find { it.jackPort.shortName === name } ?: try {
                val jackPort: JackPort = jackClient.registerPort(name, JackPortType.MIDI, JackPortFlags.JackPortIsInput)
                val midiInPort = MidiInPort(jackPort)
                this._inPorts.add(midiInPort)
                return midiInPort
            } catch (je: JackException) {
                logE("JackException while trying to get MIDI in port with name: $name")
                je.printStackTrace()
                throw je
            }
        }

        @Throws(JackException::class)
        fun removeMidiInPort(name: String) {
            val found: MidiInPort? = this._inPorts.find { it.jackPort.shortName === name }
            if (found !== null) {
                this._inPorts.remove(found)
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

        private val _outPorts = mutableListOf<AudioOutPort>()
        val outPorts: List<AudioOutPort> = _outPorts.toList()

        @Throws(JackException::class)
        fun getAudioAudioPort(name: String): AudioOutPort {
            return this._outPorts.find { it.jackPort.shortName === name } ?: try {
                val jackPort: JackPort = jackClient.registerPort(name, JackPortType.AUDIO, JackPortFlags.JackPortIsOutput)
                val audioOutPort = AudioOutPort(jackPort)
                this._outPorts.add(audioOutPort)
                return audioOutPort
            } catch (je: JackException) {
                logE("JackException while trying to get Audio out port with name: $name")
                je.printStackTrace()
                throw je
            }
        }

        @Throws(JackException::class)
        fun removeAudioOutPort(name: String) {
            val found: AudioOutPort? = this._outPorts.find { it.jackPort.shortName === name }
            if (found !== null) {
                this._outPorts.remove(found)
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