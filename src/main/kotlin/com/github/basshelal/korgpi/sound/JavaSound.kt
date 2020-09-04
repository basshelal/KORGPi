@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.sound

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Mixer

object JavaSound {
    inline fun allMixers(): List<Mixer> {
        return AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }
    }
}