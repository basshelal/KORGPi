@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.github.basshelal.korgpi.sf2

import com.github.basshelal.korgpi.extensions.F
import com.sun.media.sound.ModelByteBuffer
import java.nio.FloatBuffer
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

    val format: AudioFormat = AudioFormat(sampleRate.F, 16, 1, true, false)

    override fun toString(): String {
        return "Sample: $name"
    }

    private object TODO { // TODO: 08/01/2021
        val is24Bit = false

        // Even if only 16bit this will do, but we need to convert from 2-3 bytes to a single float
        //  representing a single sample data point, from -1.0F to 1.0F
        val sampleData: FloatBuffer = FloatBuffer.allocate(0)

        fun updateSampleData(smplBytes: ByteArray, sm24Bytes: ByteArray?) {
            // every 2 bytes in smplBytes is a point
            // if sm24 != null then the byte at that index is the 3rd part of the sample
            // Convert the 2-3 bytes into a Float somehow to make things easy for us
            TODO("Not implemented!")
        }
    }

    object SFSampleLink {
        const val MONO_SAMPLE = 1
        const val RIGHT_SAMPLE = 2
        const val LEFT_SAMPLE = 4
        const val LINKED_SAMPLE = 8
        const val ROM_MONO_SAMPLE = 0x8001
        const val ROM_RIGHT_SAMPLE = 0x8002
        const val ROM_LEFT_SAMPLE = 0x8004
        const val ROM_LINKED_SAMPLE = 0x8008
    }
}