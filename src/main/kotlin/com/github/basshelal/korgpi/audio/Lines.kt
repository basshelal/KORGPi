package com.github.basshelal.korgpi.audio

import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

typealias JLine = javax.sound.sampled.Line
typealias JLineInfo = javax.sound.sampled.Line.Info

abstract class AudioLine<T : JLine>(val jLine: T)

/**
 * An [AudioLine] that can be written to from the application, from the application's point of view this is an output
 * for the audio data.
 */
class WritableLine(val sdLine: SourceDataLine) : AudioLine<SourceDataLine>(sdLine) {

}

class ReadableLine(val tdLine: TargetDataLine) : AudioLine<TargetDataLine>(tdLine) {

}