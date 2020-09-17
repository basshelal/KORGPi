package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.extensions.ReadableLine
import com.github.basshelal.korgpi.extensions.WritableLine
import javax.sound.sampled.Line

// Annotation for functions to indicate real time critical code
@Target(AnnotationTarget.FUNCTION)
annotation class RealTimeCritical

// A thread in charge of reading from or writing to any kind of audio buffer(s).
// Code run here is real time critical
abstract class AudioThread : Thread()

abstract class LineThread<T : Line>(val line: T) : AudioThread()

class WritableLineThread(line: WritableLine) : LineThread<WritableLine>(line)

class ReadableLineThread(line: ReadableLine) : LineThread<ReadableLine>(line)