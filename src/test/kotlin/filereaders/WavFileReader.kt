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
        val reader = WavFileReader("res/test.wav")

        val byteArray = reader.byteArray
        val floatArray = reader.floatArray
        val data = reader.data

        logD(byteArray.size)
        logD(floatArray.size)
        logD(reader.sampleCount)
        logD(reader.format)

        logD(byteArray.joinToString("\n"))
        logD("=====================================")
        logD(floatArray.joinToString("\n"))
        logD("=====================================")
        data.forEachIndexed { index, it -> logD("Channel $index:\n\t${it.joinToString("\n\t")}") }


        //   floatArray.forEach { logD(it) }
    }

}