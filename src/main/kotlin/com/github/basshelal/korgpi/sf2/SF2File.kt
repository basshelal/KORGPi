package com.github.basshelal.korgpi.sf2

import java.nio.ByteBuffer

// TODO: 05/01/2021 Delete all of this, this is pointless
class SF2FileData {

    object Structs {

        data class RangesType(var lowByte: Byte, var highByte: Byte)

        object SFSampleLink {
            const val MONO_SAMPLE = 1
            const val RIGHT_SAMPLE = 2
            const val LEFT_SAMPLE = 4
            const val LINKED_SAMPLE = 8
            const val ROM_MONO_SAMPLE = 0x8001
            const val ROM_RIGHT_SAMPLE = 0x8002
            const val ROM_LEFT_SAMPLE = 0x8004
            const val ROM_LINKED_SAMPLE = 0x8008
        }

        data class Ver(var major: Int = 0, var minor: Int = 0)

        data class PresetHeader(
                var presetName: String = "",
                var preset: Int,
                var bank: Int,
                var presetBagNdx: Int,
                var library: Long,
                var genre: Long,
                var morphology: Long,
        )

        data class PresetBag(var genNdx: Int, var modNdx: Int)

        data class ModList(
                var modSrcOperator: Int,
                var modDestOperator: Int,
                var modAmount: Short,
                var modAmountSourceOperator: Int,
                var modTransportOperator: Int
        )
    }

    data class InfoChunk(
            var ifil: Structs.Ver,
            var isng: String,
            var INAM: String,
            var irom: String,
            var iver: Structs.Ver,
            var ICRD: String,
            var IENG: String,
            var IPRD: String,
            var ICOP: String,
            var ICMT: String,
            var ISFT: String)

    data class SampleDataChunk(var smpl: ByteBuffer?, var sm24: ByteBuffer?)

    data class PresetDataChunk(
            var PHDR: Structs.PresetHeader,
            var PBAG: Structs.PresetBag,
            var PMOD: Structs.ModList,
    )

    lateinit var infoChunk: InfoChunk
    lateinit var sampleDataChunk: SampleDataChunk
    lateinit var presetDataChunk: PresetDataChunk

}