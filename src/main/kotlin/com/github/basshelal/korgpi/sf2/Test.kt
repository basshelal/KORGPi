@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.github.basshelal.korgpi.sf2

import com.github.basshelal.korgpi.log.logD
import java.io.File

fun main() {
    com.sun.media.sound.SF2Soundbank(File("res/Example.sf2")).also {
        logD(it)
        logD(it.name)
        logD(it.samples.joinToString("\n"))
        logD("Total: ${it.samples.size}")
    }

    logD("----------------------------")
    logD("----------------------------")

    com.github.basshelal.korgpi.sf2.SF2Soundbank("res/Example.sf2").also {
        logD(it)
        logD(it.name)
        logD(it.samples.joinToString("\n"))
        logD("Total: ${it.samples.size}")
    }
}