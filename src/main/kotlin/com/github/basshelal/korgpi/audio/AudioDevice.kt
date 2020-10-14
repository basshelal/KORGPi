package com.github.basshelal.korgpi.audio

typealias JavaMixer = javax.sound.sampled.Mixer

// Wrapper for JavaMixer
class AudioDevice(val javaMixer: JavaMixer) {

    companion object {
        fun fromJavaMixer(javaMixer: JavaMixer): AudioDevice {
            return AudioDevice(javaMixer)
        }
    }
}