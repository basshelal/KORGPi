@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.github.basshelal.korgpi.sf2

import com.github.basshelal.korgpi.extensions.F
import com.sun.media.sound.ModelByteBuffer
import javax.sound.sampled.AudioFormat

class SF2Sample {

    var name = ""
    var startLoop: Long = 0
    var endLoop: Long = 0
    var sampleRate: Long = 44100
    var originalPitch = 60
    var pitchCorrection: Byte = 0
    var sampleLink = 0
    var sampleType = 0
    var data: ModelByteBuffer? = null
    var data24: ModelByteBuffer? = null

    val format: AudioFormat
        get() = AudioFormat(sampleRate.F, 16, 1, true, false)

    override fun toString(): String {
        return "Sample: $name"
    }
}