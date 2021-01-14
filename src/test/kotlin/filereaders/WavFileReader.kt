package filereaders

import com.github.basshelal.korgpi.filereaders.WavFileReader
import com.github.basshelal.korgpi.log.logD
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class WavFileReader {

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
        val reader = WavFileReader("res/Alesis-Fusion-Viola-C5.wav")

        val byteArray = reader.byteArray
        val floatArray = reader.floatArray

        logD(byteArray.size)
        logD(floatArray.size)
        logD(reader.sampleCount)
        logD(reader.format.isBigEndian)
        logD(reader.format)

        //   floatArray.forEach { logD(it) }
    }

}