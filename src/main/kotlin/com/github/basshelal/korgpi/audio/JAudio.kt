package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.RealTimeCritical
import com.github.basshelal.korgpi.extensions.FloatBuffer
import com.github.basshelal.korgpi.extensions.details
import com.github.basshelal.korgpi.extensions.mapAsNotNull
import com.github.basshelal.korgpi.log.Timer
import com.github.basshelal.korgpi.utils.JLine
import com.github.basshelal.korgpi.utils.JLineInfo
import com.github.basshelal.korgpi.utils.JMixer
import com.github.basshelal.korgpi.utils.JMixerInfo
import java.lang.Thread.MAX_PRIORITY
import java.nio.FloatBuffer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

class JAudioDevice(val jMixer: JMixer) {

    val isOpen: Boolean get() = jMixer.isOpen

    val jMixerInfo: JMixerInfo get() = jMixer.mixerInfo

    val availableTargetLineInfos: List<JLineInfo> get() = jMixer.targetLineInfo.asList()

    val availableTargetLines: List<JLine> get() = availableSourceLineInfos.map { getLine(it) }

    val availableSourceLineInfos: List<JLineInfo> get() = jMixer.sourceLineInfo.asList()

    val availableSourceLines: List<JLine> get() = availableSourceLineInfos.map { getLine(it) }

    val readableLines: List<ReadableLine>
        get() = availableTargetLines.mapAsNotNull<TargetDataLine>().map { ReadableLine(it) }

    val writableLines: List<WritableLine>
        get() = availableSourceLines.mapAsNotNull<SourceDataLine>().map { WritableLine(it) }

    val openReadableLines: List<ReadableLine>
        get() = jMixer.targetLines.filterIsInstance<TargetDataLine>().map { ReadableLine(it) }

    val openWritableLines: List<WritableLine>
        get() = jMixer.sourceLines.filterIsInstance<SourceDataLine>().map { WritableLine(it) }

    val details: String
        get() = """Audio Device:
        |  name: ${jMixerInfo.name}
        |  type: ${jMixer.javaClass.simpleName}
        |  version: ${jMixerInfo.version}
        |  vendor: ${jMixerInfo.vendor}
        |  description: ${jMixerInfo.description}
        |  isOpen: ${isOpen}
        |  available readable lines: ${availableTargetLineInfos.size}
        |  available writable lines: ${availableSourceLineInfos.size}
        |  open readable lines: ${openReadableLines.size}
        |  open writable lines: ${openWritableLines.size}
        """.trimMargin()

    fun open() = jMixer.open()

    fun close() = jMixer.close()

    fun getLine(jLineInfo: JLineInfo): JLine = jMixer.getLine(jLineInfo)
}

abstract class AudioLine<T : JLine>(val jLine: T) {
    fun open() = jLine.open()
}

/**
 * An [AudioLine] that can be written to from the application, from the application's point of view this is an output
 * for the audio data.
 */
class WritableLine(sdLine: SourceDataLine) : AudioLine<SourceDataLine>(sdLine) {

    private val bytes: ByteArray = ByteArray(jLine.bufferSize)

    // TODO: 10/02/2021 What is buffer?? Initialize it and set it
    val buffer: FloatBuffer

    init {
        jLine.open(null, 0) // set buffer size here, if line was open close and reopen

        val bufferSize = jLine.bufferSize // buffer size bytes, we use floats so we need to convert!
        buffer = FloatBuffer(bufferSize)

    }

    @RealTimeCritical
    fun process() {
        while (jLine.available() > 0) {
            jLine.write(null, 0, 0) // write max possible bytes without blocking
        }
    }

    val details: String get() = jLine.details

}

class ReadableLine(tdLine: TargetDataLine) : AudioLine<TargetDataLine>(tdLine) {

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