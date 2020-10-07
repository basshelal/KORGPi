package com.github.basshelal.korgpi.audio

typealias JavaMixer = javax.sound.sampled.Mixer

// Wrapper for JavaMixer
class AudioDevice {

    companion object {
        fun fromJavaMixer(javaMixer: JavaMixer): AudioDevice {
            TODO()
        }
    }
}