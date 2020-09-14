@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.log

import com.github.basshelal.korgpi.extensions.now
import com.github.basshelal.korgpi.extensions.nowNanos

object Timer {

    var resolution: Resolution = Resolution.MILLI

    var startTime = 0L
        private set
    var stopTime = 0L
        private set
    var duration = 0L
        private set

    private inline val get: Long
        get() = if (resolution == Resolution.MILLI) now else nowNanos

    fun start(): Long {
        startTime = get
        return startTime
    }

    fun stop(): Long {
        stopTime = get
        duration = stopTime - startTime
        return duration
    }

    enum class Resolution { MILLI, NANO }
}