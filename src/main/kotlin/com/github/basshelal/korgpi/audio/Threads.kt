package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.extensions.now
import com.github.basshelal.korgpi.log.logD

// Annotation for functions to indicate real time critical code
@Target(AnnotationTarget.FUNCTION)
annotation class RealTimeCritical

// A thread in charge of reading from or writing to any kind of audio buffer(s).
// Code run here is real time critical
// Audio Threads are by default max priority
abstract class AudioThread : Thread() {

    init {
        this.priority = MAX_PRIORITY
    }

    abstract val buffer: ByteArray

    @RealTimeCritical
    abstract override fun run()
}

abstract class LineThread<T : JavaAudioLine<*>>(val line: T) : AudioThread()

class WritableLineThread(
        line: WritableLine,
        override val buffer: ByteArray = ByteArray(line.line.bufferSize)
) : LineThread<WritableLine>(line) {

    @RealTimeCritical
    override fun run() {
        runForever {
            line.line.write(buffer, 0, buffer.size)
        }
    }
}

class ReadableLineThread(
        line: ReadableLine,
        override val buffer: ByteArray = ByteArray(line.line.bufferSize)
) : LineThread<ReadableLine>(line) {

    @RealTimeCritical
    override fun run() {
        runForever {
            line.line.read(buffer, 0, buffer.size)
        }
    }
}

class ReadWriteLineThread(
        val readableLine: ReadableLine,
        val writableLine: WritableLine,
        override val buffer: ByteArray
) : AudioThread() {

    var running = true

    @RealTimeCritical
    override fun run() {
        runForever {
            logD(now)
            if (!running) return
            readableLine.line.read(buffer, 0, buffer.size)
            writableLine.line.write(buffer, 0, buffer.size)
        }
    }
}

inline fun runForever(block: () -> Unit) {
    while (true) block()
}