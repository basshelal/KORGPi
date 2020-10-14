package com.github.basshelal.korgpi.audio

import javax.sound.sampled.Line
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

abstract class AudioLine<T : Line>(val line: T)

/**
 * An [AudioLine] that can be written to from the application, from the application's point of view this is an output
 * for the audio data.
 */
class WritableLine(line: SourceDataLine) : AudioLine<SourceDataLine>(line)

class ReadableLine(line: TargetDataLine) : AudioLine<TargetDataLine>(line)