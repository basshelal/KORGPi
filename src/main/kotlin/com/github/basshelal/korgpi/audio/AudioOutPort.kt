package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.RealTimeCritical
import org.jaudiolibs.jnajack.JackPort
import java.nio.FloatBuffer

abstract class AudioOutPort {
    abstract val audioProcessors: MutableList<AudioProcessor>

    @RealTimeCritical
    abstract fun process()
}

class JackAudioOutPort(val jackPort: JackPort) : AudioOutPort() {

    override val audioProcessors: MutableList<AudioProcessor> = mutableListOf()

    @RealTimeCritical
    override fun process() {
        audioProcessors.forEach { it.processAudio(jackPort.floatBuffer) }
    }
}

class JavaAudioOutPort(val writableLine: WritableLine) : AudioOutPort() {

    override val audioProcessors: MutableList<AudioProcessor> = mutableListOf()

    @RealTimeCritical
    override fun process() {
        audioProcessors.forEach { it.processAudio(writableLine.buffer) }
    }
}

interface AudioProcessor {

    @RealTimeCritical
    fun processAudio(buffer: FloatBuffer)

    operator fun invoke(buffer: FloatBuffer) = this.processAudio(buffer)

    companion object {
        inline operator fun invoke(crossinline processAudio: (buffer: FloatBuffer) -> Unit): AudioProcessor =
                object : AudioProcessor {
                    override fun processAudio(buffer: FloatBuffer) = processAudio(buffer)
                }
    }
}
