package com.github.basshelal.korgpi.sf2

import com.github.basshelal.korgpi.extensions.B
import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.extensions.S

open class SF2Region {

    val generators: MutableMap<Int, Short> = mutableMapOf()

    val modulators: MutableList<SF2Modulator> = mutableListOf()

    operator fun contains(generator: Int): Boolean = generators.contains(generator)

    fun getShort(generator: Int): Short {
        return generators[generator] ?: getDefaultValue(generator)
    }

    fun putShort(generator: Int, value: Short) {
        generators[generator] = value
    }

    fun getBytes(generator: Int): ByteArray {
        val value = getInteger(generator)
        val bytes = ByteArray(2)
        bytes[0] = (0xFF and value).B
        bytes[1] = (0xFF00 and value shr 8).B
        return bytes
    }

    fun putBytes(generator: Int, bytes: ByteArray) {
        generators[generator] = (bytes[0] + (bytes[1].I shl 8)).S
    }

    fun getInteger(generator: Int): Int {
        return 0xFFFF and getShort(generator).I
    }

    fun putInteger(generator: Int, value: Int) {
        generators[generator] = value.S
    }

    companion object {

        fun getDefaultValue(generator: Int): Short {
            return when (generator) {
                8 -> 13500.S
                21 -> (-12000).S
                23 -> (-12000).S
                25 -> (-12000).S
                26 -> (-12000).S
                27 -> (-12000).S
                28 -> (-12000).S
                30 -> (-12000).S
                33 -> (-12000).S
                34 -> (-12000).S
                35 -> (-12000).S
                36 -> (-12000).S
                38 -> (-12000).S
                43 -> 0x7F00.S
                44 -> 0x7F00.S
                46 -> (-1).S
                47 -> (-1).S
                56 -> 100.S
                58 -> (-1).S
                else -> 0
            }
        }

        const val GENERATOR_STARTADDRSOFFSET = 0

        const val GENERATOR_ENDADDRSOFFSET = 1

        const val GENERATOR_STARTLOOPADDRSOFFSET = 2

        const val GENERATOR_ENDLOOPADDRSOFFSET = 3

        const val GENERATOR_STARTADDRSCOARSEOFFSET = 4

        const val GENERATOR_MODLFOTOPITCH = 5

        const val GENERATOR_VIBLFOTOPITCH = 6

        const val GENERATOR_MODENVTOPITCH = 7

        const val GENERATOR_INITIALFILTERFC = 8

        const val GENERATOR_INITIALFILTERQ = 9

        const val GENERATOR_MODLFOTOFILTERFC = 10

        const val GENERATOR_MODENVTOFILTERFC = 11

        const val GENERATOR_ENDADDRSCOARSEOFFSET = 12

        const val GENERATOR_MODLFOTOVOLUME = 13

        const val GENERATOR_UNUSED1 = 14

        const val GENERATOR_CHORUSEFFECTSSEND = 15

        const val GENERATOR_REVERBEFFECTSSEND = 16

        const val GENERATOR_PAN = 17

        const val GENERATOR_UNUSED2 = 18

        const val GENERATOR_UNUSED3 = 19

        const val GENERATOR_UNUSED4 = 20

        const val GENERATOR_DELAYMODLFO = 21

        const val GENERATOR_FREQMODLFO = 22

        const val GENERATOR_DELAYVIBLFO = 23

        const val GENERATOR_FREQVIBLFO = 24

        const val GENERATOR_DELAYMODENV = 25

        const val GENERATOR_ATTACKMODENV = 26

        const val GENERATOR_HOLDMODENV = 27

        const val GENERATOR_DECAYMODENV = 28

        const val GENERATOR_SUSTAINMODENV = 29

        const val GENERATOR_RELEASEMODENV = 30

        const val GENERATOR_KEYNUMTOMODENVHOLD = 31

        const val GENERATOR_KEYNUMTOMODENVDECAY = 32

        const val GENERATOR_DELAYVOLENV = 33

        const val GENERATOR_ATTACKVOLENV = 34

        const val GENERATOR_HOLDVOLENV = 35

        const val GENERATOR_DECAYVOLENV = 36

        const val GENERATOR_SUSTAINVOLENV = 37

        const val GENERATOR_RELEASEVOLENV = 38

        const val GENERATOR_KEYNUMTOVOLENVHOLD = 39

        const val GENERATOR_KEYNUMTOVOLENVDECAY = 40

        const val GENERATOR_INSTRUMENT = 41

        const val GENERATOR_RESERVED1 = 42

        const val GENERATOR_KEYRANGE = 43

        const val GENERATOR_VELRANGE = 44

        const val GENERATOR_STARTLOOPADDRSCOARSEOFFSET = 45

        const val GENERATOR_KEYNUM = 46

        const val GENERATOR_VELOCITY = 47

        const val GENERATOR_INITIALATTENUATION = 48

        const val GENERATOR_RESERVED2 = 49

        const val GENERATOR_ENDLOOPADDRSCOARSEOFFSET = 50

        const val GENERATOR_COARSETUNE = 51

        const val GENERATOR_FINETUNE = 52

        const val GENERATOR_SAMPLEID = 53

        const val GENERATOR_SAMPLEMODES = 54

        const val GENERATOR_RESERVED3 = 55

        const val GENERATOR_SCALETUNING = 56

        const val GENERATOR_EXCLUSIVECLASS = 57

        const val GENERATOR_OVERRIDINGROOTKEY = 58

        const val GENERATOR_UNUSED5 = 59

        const val GENERATOR_ENDOPR = 60
    }
}

