package filereaders

import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.filereaders.FourBytes
import com.github.basshelal.korgpi.filereaders.WavFileReader
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.log.logE
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
        logD("=====================================")
        logD(reader.floatRange)
        logD(reader.intRange)

        // 80808080 to 7f7f8080

        logE(Float.fromBits(0x80808080.toInt()))
        logE(Float.fromBits(0x7f7f8080))

        logE(0x80808080.I)
        logE(0x7f7f8080)

        //   floatArray.forEach { logD(it) }
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