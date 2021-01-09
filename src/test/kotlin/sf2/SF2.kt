@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package sf2

import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.log.logD
import com.github.basshelal.korgpi.log.logDAll
import mustEqual
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder


@DisplayName("SoundFont Tests")
class SF2 {

    val JSoundbank = com.sun.media.sound.SF2Soundbank(File("res/Example.sf2"))
    val KSoundbank = com.github.basshelal.korgpi.sf2.SF2Soundbank("res/Example.sf2")

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
    fun `Variables are equal`() {
        JSoundbank.name mustEqual KSoundbank.name

        JSoundbank.samples.size mustEqual KSoundbank.samples.size

        JSoundbank.instruments.size mustEqual KSoundbank.instruments.size
        JSoundbank.layers.size mustEqual KSoundbank.layers.size


        KSoundbank.samples.size mustEqual 864
        //  KSoundbank.instruments.size mustEqual 304

        // Polyphone calls these Presets, we have 275 Polyphone Presets

        // KSoundbank.samples.forEach { logD("${it.name}\n") }

        val sample = KSoundbank.samples.find { it.name == "TR-808 Click" }

        require(sample != null)

        val array = sample.data?.array()!!

        val max = array.maxOrNull()
        val min = array.minOrNull()


        logD(array.size)
        logD(max)
        logD(min)


        val intArray = mutableListOf<Int>()

        array.forEachIndexed { index, it ->
            if (index % 2 == 0) {
                val b1 = it
                val b2 = array[index + 1]

                val int: Int = ((b1.I and 0xFF) shl 8) or (b2.I and 0xFF)

                val intt = ByteBuffer.wrap(byteArrayOf(b1, b2)).order(ByteOrder.LITTLE_ENDIAN).short

                intArray.add(intt.I)

                // logD(int)

                // append bits, depending on byte ordering, SF2 is little endian
                // then change range to be from 16Bit Min (Short.MIN_VALUE) to 16Bit Max (Short.MAX_VALUE)
                // to be from -1.0F to 1.0F

                //  logD("b1: $b1, b2: $b2")
            }
        }

        logD(intArray.size)
        logD(intArray.maxOrNull())
        logD(intArray.minOrNull())


        File("./ints").writeText(intArray.joinToString("\n"))

    }

    @Test
    fun `Read All Data`() {
        KSoundbank.apply {
            logDAll(
                    "name:\n $name",
                    "product:\n $product",
                    "comments:\n $comments"
            )
        }
    }


}