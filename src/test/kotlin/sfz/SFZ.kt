package sfz

import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.sfz.SFZFileReader
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("SoundFont Tests")
class SFZ {

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
    fun `Read All Data`() {

        val reader = SFZFileReader("res/jRhodes3c-sfz/_jRhodes-stereo-looped.sfz")

        reader.headers.forEach {
            logD(it.key)
            it.opcodes.forEach {
                logD("\t${it.key} : ${it.value}")
            }
        }
    }

}