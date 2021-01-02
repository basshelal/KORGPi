@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package sf2

import mustEqual
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

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
    fun `Names are equal`() {
        JSoundbank.name mustEqual KSoundbank.name
    }


}