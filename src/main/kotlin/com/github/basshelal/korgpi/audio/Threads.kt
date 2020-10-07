package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.log.Timer
import com.github.basshelal.korgpi.log.logD
import java.lang.Thread.MAX_PRIORITY

// Annotation for functions to indicate real time critical code
@Target(AnnotationTarget.FUNCTION)
annotation class RealTimeCritical

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
        override val buffer: ByteArray = ByteArray(line.line.bufferSize)
) : LineThread<WritableLine>(line) {

    @RealTimeCritical
    override fun onRun() {
        line.line.write(buffer, 0, buffer.size)
    }
}

class ReadableLineThread(
        line: ReadableLine,
        override val buffer: ByteArray = ByteArray(line.line.bufferSize)
) : LineThread<ReadableLine>(line) {

    @RealTimeCritical
    override fun onRun() {
        line.line.read(buffer, 0, buffer.size)
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
        readableLine.line.read(buffer, 0, buffer.size)
        writableLine.line.write(buffer, 0, buffer.size)
        logD("${Timer.stop()} millis")
    }
}