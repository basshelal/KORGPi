package com.github.basshelal.korgpi.mixers

import com.github.basshelal.korgpi.extensions.openClient
import com.github.basshelal.korgpi.log.logE
import org.jaudiolibs.jnajack.Jack
import org.jaudiolibs.jnajack.JackClient
import org.jaudiolibs.jnajack.JackException
import org.jaudiolibs.jnajack.JackPort
import org.jaudiolibs.jnajack.JackPortFlags
import org.jaudiolibs.jnajack.JackPortType

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

    object Midi {

        private val _inPorts = mutableListOf<JackPort>()
        val inPorts: List<JackPort> get() = _inPorts.toList()

        @Throws(JackException::class)
        fun getMidiInPort(name: String): JackPort {
            return this._inPorts.find { it.shortName === name } ?: try {
                val port = jackClient.registerPort(name, JackPortType.MIDI, JackPortFlags.JackPortIsInput)
                this._inPorts.add(port)
                return port
            } catch (je: JackException) {
                logE("JackException while trying to get MIDI in port with name: $name")
                je.printStackTrace()
                throw je
            }
        }

        @Throws(JackException::class)
        fun removeMidiInPort(name: String) {
            val found: JackPort? = this._inPorts.find { it.shortName === name }
            if (found !== null) {
                this._inPorts.remove(found)
                try {
                    jackClient.unregisterPort(found)
                } catch (je: JackException) {
                    logE("JackException while trying to remove MIDI in port with name: $name")
                    je.printStackTrace()
                    throw je
                }
            }
        }

    }

    object Audio {

        private val _outPorts = mutableListOf<JackPort>()
        val outPorts: List<JackPort> = _outPorts.toList()

        @Throws(JackException::class)
        fun getAudioAudioPort(name: String): JackPort {
            return this._outPorts.find { it.shortName === name } ?: try {
                val port = jackClient.registerPort(name, JackPortType.AUDIO, JackPortFlags.JackPortIsOutput)
                this._outPorts.add(port)
                return port
            } catch (je: JackException) {
                logE("JackException while trying to get Audio out port with name: $name")
                je.printStackTrace()
                throw je
            }
        }

        @Throws(JackException::class)
        fun removeAudioOutPort(name: String) {
            val found: JackPort? = this._outPorts.find { it.shortName === name }
            if (found !== null) {
                this._outPorts.remove(found)
                try {
                    jackClient.unregisterPort(found)
                } catch (je: JackException) {
                    logE("JackException while trying to remove Audio out port with name: $name")
                    je.printStackTrace()
                    throw je
                }
            }
        }

    }

}