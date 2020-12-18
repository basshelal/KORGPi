package com.github.basshelal.korgpi.audio

import com.github.basshelal.korgpi.extensions.forEachIndexed
import org.jaudiolibs.jnajack.JackPort
import java.nio.FloatBuffer

class AudioOutPort(var jackPort: JackPort) {

    val callbacks: MutableList<(FloatBuffer) -> Unit> = mutableListOf()

    inline val floatBuffer: FloatBuffer get() = jackPort.floatBuffer

    @RealTimeCritical
    fun process() {

        floatBuffer.forEachIndexed { value, index ->
            //   floatBuffer[index] = 0.2F * Random.nextFloat()
        }

        callbacks.forEach { it(floatBuffer) }
    }
}



