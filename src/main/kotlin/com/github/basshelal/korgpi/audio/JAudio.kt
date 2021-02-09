package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.RealTimeCritical
import com.github.basshelal.korgpi.log.Timer
import java.lang.Thread.MAX_PRIORITY
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

typealias JMixer = javax.sound.sampled.Mixer
typealias JMixerInfo = javax.sound.sampled.Mixer.Info
typealias JLine = javax.sound.sampled.Line
typealias JLineInfo = javax.sound.sampled.Line.Info

class JAudioDevice(val jMixer: JMixer) {

    val isOpen: Boolean get() = jMixer.isOpen

    val availableReadableLines: List<JLineInfo>
        get() {
            return jMixer.targetLineInfo.asList()
        }

    val availableWritableLines: List<JLineInfo>
        get() {
            return jMixer.sourceLineInfo.asList()
        }

    val openReadableLines: List<ReadableLine>
        get() {
            return jMixer.targetLines.filterIsInstance<TargetDataLine>().map { ReadableLine(it) }
        }

    val openWritableLines: List<WritableLine>
        get() {
            return jMixer.sourceLines.filterIsInstance<SourceDataLine>().map { WritableLine(it) }
        }

    val jInfo: JMixerInfo
        get() {
            return jMixer.mixerInfo
        }

    val details: String
        get() = """Audio Device:
        |  name: ${jInfo.name}
        |  type: ${jMixer.javaClass.simpleName}
        |  version: ${jInfo.version}
        |  vendor: ${jInfo.vendor}
        |  description: ${jInfo.description}
        |  isOpen: ${isOpen}
        |  available readable lines: ${availableReadableLines.size}
        |  available writable lines: ${availableWritableLines.size}
        |  open readable lines: ${openReadableLines.size}
        |  open writable lines: ${openWritableLines.size}
        """.trimMargin()
}

abstract class AudioLine<T : JLine>(val jLine: T)

/**
 * An [AudioLine] that can be written to from the application, from the application's point of view this is an output
 * for the audio data.
 */
class WritableLine(val sdLine: SourceDataLine) : AudioLine<SourceDataLine>(sdLine) {

}

class ReadableLine(val tdLine: TargetDataLine) : AudioLine<TargetDataLine>(tdLine) {

}

// Wrapper for Java Threads
abstract class BaseThread {

    protected val thread: Thread = object : Thread() {
        override fun run() {
            while (true) {
                if (!isRunning) break
                onRun()
            }
            onKilled()
        }
    }

    val id: Long = thread.id

    open var isRunning: Boolean = true

    open fun start() = thread.start()

    open fun kill() {
        isRunning = false
    }

    abstract fun onRun()

    open fun onKilled() {}

}

// A thread in charge of reading from or writing to any kind of audio buffer(s).
// Code run here is real time critical
// Audio Threads are by default max priority
abstract class AudioThread : BaseThread() {

    init {
        this.thread.priority = MAX_PRIORITY
    }

    abstract val buffer: ByteArray

    @RealTimeCritical
    abstract override fun onRun()
}

abstract class LineThread<T : AudioLine<*>>(val line: T) : AudioThread()

class WritableLineThread(
        line: WritableLine,
        override val buffer: ByteArray = ByteArray(line.jLine.bufferSize)
) : LineThread<WritableLine>(line) {

    @RealTimeCritical
    override fun onRun() {
        line.jLine.write(buffer, 0, buffer.size)
    }
}

class ReadableLineThread(
        line: ReadableLine,
        override val buffer: ByteArray = ByteArray(line.jLine.bufferSize)
) : LineThread<ReadableLine>(line) {

    @RealTimeCritical
    override fun onRun() {
        line.jLine.read(buffer, 0, buffer.size)
    }
}

class ReadWriteLineThread(
        val readableLine: ReadableLine,
        val writableLine: WritableLine,
        override val buffer: ByteArray
) : AudioThread() {

    init {
        Timer.resolution = Timer.Resolution.MILLI
    }

    @RealTimeCritical
    override fun onRun() {
        Timer.start()
        readableLine.jLine.read(buffer, 0, buffer.size)
        writableLine.jLine.write(buffer, 0, buffer.size)
        //logD("${Timer.stop()} millis")
    }
}