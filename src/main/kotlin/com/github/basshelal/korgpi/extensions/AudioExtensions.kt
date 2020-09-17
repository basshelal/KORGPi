@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import com.github.basshelal.korgpi.audio.JavaAudio
import javax.sound.sampled.DataLine
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

typealias WritableLine = SourceDataLine
typealias ReadableLine = TargetDataLine

inline fun Mixer.allLines(): List<Line> {
    return this.sourceLines.plus(this.targetLines).asList()
}

inline val Mixer.details: String
    get() = mixerInfo.let {
        """Name: ${it.name}
        |version: ${it.version}
        |vendor: ${it.vendor}
        |description: ${it.description}
        |type: ${this.javaClass.simpleName}
        |source lines: ${sourceLines.size}
        |target lines: ${targetLines.size}
        """.trimMargin()
    }

inline val Line.mixer: Mixer?
    get() = JavaAudio.allMixers().find { this in it.allLines() }

inline val DataLine.details: String
    get() = (lineInfo as DataLine.Info).let {
        """Type: ${it.lineClass.simpleName}
        |formats: ${it.formats.size}
        |min buffer size: ${it.minBufferSize}
        |max buffer size: ${it.maxBufferSize}
        |buffer size: ${this.bufferSize}
        |format: ${this.format}
        """.trimMargin()
    }