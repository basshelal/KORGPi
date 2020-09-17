package com.github.basshelal.korgpi.audio

import javax.sound.sampled.Line
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

abstract class JavaAudioLine<T : Line>(val line: T) {
    // abstract val thread: LineThread<*>
}

class WritableLine(line: SourceDataLine) : JavaAudioLine<SourceDataLine>(line) {
    //  override val thread: LineThread<WritableLine> by lazy { WritableLineThread(this) }
}

class ReadableLine(line: TargetDataLine) : JavaAudioLine<TargetDataLine>(line) {
    //  override val thread: LineThread<ReadableLine> by lazy { ReadableLineThread(this) }
}