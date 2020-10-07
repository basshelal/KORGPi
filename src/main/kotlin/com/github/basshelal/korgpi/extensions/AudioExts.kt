@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import com.github.basshelal.korgpi.mixers.AudioMixer
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
    get() = AudioMixer.allAudioDevices().find { this in it.allLines() }

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