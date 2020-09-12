@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi

import javafx.stage.Stage
import javax.sound.midi.ShortMessage
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer

inline val Number.I: Int
    get() = this.toInt()

inline val Number.D: Double
    get() = this.toDouble()

inline val Number.F: Float
    get() = this.toFloat()

inline val Number.L: Long
    get() = this.toLong()

inline val Number.B: Byte
    get() = this.toByte()

inline val now: Long get() = System.currentTimeMillis()

inline val nowNanos: Long get() = System.nanoTime()

inline fun <reified T : Throwable> ignoreException(func: () -> Any) {
    try {
        func()
    } catch (e: Throwable) {
        if (e !is T) throw e
    }
}

inline fun Mixer.allLines(): List<Line> {
    return this.sourceLines.plus(this.targetLines).asList()
}

inline val Mixer.info: String
    get() = mixerInfo.let {
        "Name: ${it.name}\nversion: ${it.version}\nvendor: ${it.vendor}\ndescription: ${it.description}\n"
    }

inline val ShortMessage.info: String
    get() {
        return "Command: $command, channel: $channel, Data1: $data1, Data2: $data2"
    }

inline var Stage.dimensions: Pair<Number, Number>
    set(value) {
        width = value.first.toDouble()
        height = value.second.toDouble()
    }
    @Deprecated("No Getter", level = DeprecationLevel.ERROR)
    get() = throw NotImplementedError("No Getter")