@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import com.github.basshelal.korgpi.mixers.JavaMixer
import javax.sound.midi.MidiDevice
import javax.sound.midi.ShortMessage
import javax.sound.sampled.DataLine
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer

inline fun Mixer.allLines(): List<Line> {
    return this.sourceLines.plus(this.targetLines).asList()
}

inline val Mixer.details: String
    get() = mixerInfo.let {
        """Mixer:
        |type: ${this.javaClass.simpleName}
        |name: ${it.name}
        |version: ${it.version}
        |vendor: ${it.vendor}
        |description: ${it.description}
        |source lines: ${sourceLines.size}
        |target lines: ${targetLines.size}
        """.trimMargin()
    }

inline val Line.mixer: Mixer?
    get() = JavaMixer.jMixers.find { this in it.allLines() }

inline val DataLine.details: String
    get() = (lineInfo as DataLine.Info).let {
        """DataLine:
        |type: ${this.javaClass.simpleName}
        |mixer name: ${this.mixer?.mixerInfo?.name}
        |formats: ${it.formats.size}
        |min buffer size: ${it.minBufferSize}
        |max buffer size: ${it.maxBufferSize}
        |buffer size: ${this.bufferSize}
        |format: ${this.format}
        """.trimMargin()
    }

inline val MidiDevice.details: String
    get() = deviceInfo.let {
        """MidiDevice:
        |type: ${this.javaClass.simpleName}
        |name: ${it.name}
        |version: ${it.version}
        |vendor: ${it.vendor}
        |description: ${it.description}
        |isOpen: ${this.isOpen}
        |receivers: ${this.receivers.size}
        |transmitters: ${this.transmitters.size}
        |maxReceivers: ${this.maxReceivers}
        |maxTransmitters: ${this.maxTransmitters}
        """.trimMargin()
    }

inline val ShortMessage.details: String
    get() {
        return "Command: $command, channel: $channel, Data1: $data1, Data2: $data2"
    }