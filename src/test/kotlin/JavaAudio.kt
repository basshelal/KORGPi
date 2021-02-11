import com.github.basshelal.korgpi.extensions.details
import com.github.basshelal.korgpi.extensions.mapAsNotNull
import com.github.basshelal.korgpi.log.log
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.mixers.JavaMixer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
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
        JavaMixer.audioDevices.forEach { logD(it.details) }

        val defaultDevice = JavaMixer.defaultAudioDevice

        defaultDevice?.open()

        defaultDevice?.availableSourceLineInfos?.log()

        defaultDevice?.availableSourceLines?.mapAsNotNull<SourceDataLine>()?.map { it.details }.log()

        defaultDevice?.writableLines?.map { it.details }.log()

        defaultDevice?.details.log()

        defaultDevice?.openWritableLines.log()

        defaultDevice?.jMixer?.close()
    }

}