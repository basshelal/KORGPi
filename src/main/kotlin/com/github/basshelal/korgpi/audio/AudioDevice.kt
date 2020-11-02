@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.audio

import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

typealias JMixer = javax.sound.sampled.Mixer
typealias JMixerInfo = javax.sound.sampled.Mixer.Info

// Wrapper for JavaMixer
class AudioDevice(val jMixer: JMixer) {

    val isOpen: Boolean get() = jMixer.isOpen

    val availableReadableLines: List<JLineInfo>
        get() {
            return jMixer.targetLineInfo.asList()
        }

    val availableWritableLines: List<JLineInfo>
        get() {
            return jMixer.sourceLineInfo.asList()
        }

    val openReadableLines: List<ReadableLine>
        get() {
            return jMixer.targetLines.filterIsInstance<TargetDataLine>().map { ReadableLine(it) }
        }

    val openWritableLines: List<WritableLine>
        get() {
            return jMixer.sourceLines.filterIsInstance<SourceDataLine>().map { WritableLine(it) }
        }

    val jInfo: JMixerInfo
        get() {
            return jMixer.mixerInfo
        }

    val details: String
        get() = """Audio Device:
        |  name: ${jInfo.name}
        |  type: ${jMixer.javaClass.simpleName}
        |  version: ${jInfo.version}
        |  vendor: ${jInfo.vendor}
        |  description: ${jInfo.description}
        |  isOpen: ${isOpen}
        |  available readable lines: ${availableReadableLines.size}
        |  available writable lines: ${availableWritableLines.size}
        |  open readable lines: ${openReadableLines.size}
        |  open writable lines: ${openWritableLines.size}
        """.trimMargin()
}