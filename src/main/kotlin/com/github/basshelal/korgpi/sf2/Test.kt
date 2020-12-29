@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.github.basshelal.korgpi.sf2

import com.github.basshelal.korgpi.log.logD
import com.sun.media.sound.SF2Soundbank
import java.io.File

fun main() {
    SF2Soundbank(File("/home/bassam/dls/SalC5Light2.sf2")).also {
        logD(it)
        logD(it.name)
        logD(it.creationDate)
        logD(it.samples.joinToString())
        it.samples.forEach {
            logD(it.dataBuffer)
        }
    }
}