import com.github.basshelal.korgpi.audio.AudioProcessor
import com.github.basshelal.korgpi.audio.JavaAudioOutPort
import com.github.basshelal.korgpi.extensions.addOnSystemShutdownCallback
import com.github.basshelal.korgpi.extensions.details
import com.github.basshelal.korgpi.extensions.forEach
import com.github.basshelal.korgpi.extensions.mapAsNotNull
import com.github.basshelal.korgpi.log.log
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.mixers.JavaMixer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.FloatBuffer
import javax.sound.sampled.SourceDataLine


class JavaAudio {

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
        }
    }


    @Test
    fun test() {

        val defaultDevice = JavaMixer.defaultAudioDevice

        require(defaultDevice != null)

        defaultDevice.open()

        defaultDevice.availableSourceLines.mapAsNotNull<SourceDataLine>().map { it.details }.log()

        defaultDevice.writableLines.map { it.details }.log()

        defaultDevice.details.log()

        defaultDevice.openWritableLines.log()

        val writableLine = defaultDevice.writableLines.first()

        val outPort = JavaAudioOutPort(writableLine)

        outPort.audioProcessors.add(AudioProcessor { buffer: FloatBuffer ->
            buffer.forEach { logD(it) }
        })

        defaultDevice.jMixer.close()

        addOnSystemShutdownCallback {
            writableLine.close()
            defaultDevice.close()
        }

        Thread.sleep(Long.MAX_VALUE)
    }

}