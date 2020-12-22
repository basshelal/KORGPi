package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.RealTimeCritical
import org.jaudiolibs.jnajack.JackPort
import java.nio.FloatBuffer

class AudioOutPort(val jackPort: JackPort) {

    val audioProcessors: MutableList<AudioProcessor> = mutableListOf()

    @RealTimeCritical
    fun process() {
        audioProcessors.forEach { it.processAudio(jackPort.floatBuffer) }
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
