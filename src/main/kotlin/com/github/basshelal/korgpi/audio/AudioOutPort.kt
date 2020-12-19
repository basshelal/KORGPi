package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.RealTimeCritical
import com.github.basshelal.korgpi.extensions.forEach
import com.github.basshelal.korgpi.log.logD
import org.jaudiolibs.jnajack.JackPort
import java.nio.FloatBuffer

class AudioOutPort(var jackPort: JackPort) {

    val callbacks: MutableList<(FloatBuffer) -> Unit> = mutableListOf()

    inline val floatBuffer: FloatBuffer get() = jackPort.floatBuffer

    @RealTimeCritical
    fun process() {

        callbacks.forEach { it(floatBuffer) }

        floatBuffer.forEach { if (it != 0.0F) logD(it) }
    }
}
