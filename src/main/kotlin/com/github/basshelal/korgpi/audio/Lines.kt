package com.github.basshelal.korgpi.audio

import javax.sound.sampled.Line
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

abstract class AudioLine<T : Line>(val line: T)

class WritableLine(line: SourceDataLine) : AudioLine<SourceDataLine>(line)

class ReadableLine(line: TargetDataLine) : AudioLine<TargetDataLine>(line)