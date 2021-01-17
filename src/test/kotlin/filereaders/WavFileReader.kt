package filereaders

import com.github.basshelal.korgpi.filereaders.FourBytes
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

        logD("byteArray.size", byteArray.size)
        logD("floatArray.size", floatArray.size)
        logD("reader.sampleCount", reader.sampleCount)
        logD("reader.format", reader.format)

        logD("byteArray:\n", byteArray.joinToString("\n"))
        logD()







        logD("floatArray:\n", floatArray.joinToString("\n"))
        logD()
        logD("data:\n")
        data.forEachIndexed { index, it -> logD("Channel $index:\n\t${it.joinToString("\n\t")}") }
        logD()
        logD("reader.floatRange:", reader.floatRange)
        logD("reader.intRange:", reader.intRange)


    }

    @Test
    fun `FourBytes test`() {
        val buffer = FourBytes(isBigEndian = false)

        buffer.put(Byte.MAX_VALUE).put(Byte.MAX_VALUE).put(Byte.MAX_VALUE).put(Byte.MAX_VALUE)

        logD(buffer.int)
        logD(buffer.float)

        buffer.clear()
        buffer.put(0).put(0).put(0).put(0)

        logD(buffer.int)
        logD(buffer.float)
    }

}