@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.audio

typealias JMixer = javax.sound.sampled.Mixer
typealias JMixerInfo = javax.sound.sampled.Mixer.Info

// Wrapper for JavaMixer
class AudioDevice(val jMixer: JMixer) {

    val availableReadableLines: List<ReadableLine>
        get() {
            jMixer.targetLineInfo
            return emptyList()
        }

    val availableWritableLines: List<WritableLine>
        get() {
            jMixer.sourceLineInfo
            return emptyList()
        }

    val openReadableLines: List<ReadableLine>
        get() {
            jMixer.targetLines
            return emptyList()
        }

    val openWritableLines: List<WritableLine>
        get() {
            jMixer.sourceLines
            return emptyList()
        }

    val jInfo: JMixerInfo
        get() {
            return jMixer.mixerInfo
        }

    val details: String
        get() {
            return ""
        }

}