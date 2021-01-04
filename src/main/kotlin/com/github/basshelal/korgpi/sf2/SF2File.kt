package com.github.basshelal.korgpi.sf2

import java.nio.ByteBuffer

class SF2FileData {

    class InfoChunk {

        data class Ver(var major: Int = 0, var minor: Int = 0)

        var ifil: Ver = Ver()
        var isng: String = "EMU8000"
        var INAM: String = ""
        var irom: String = ""
        var iver: Ver = Ver()
        var ICRD: String = ""
        var IENG: String = ""
        var IPRD: String = ""
        var ICOP: String = ""
        var ICMT: String = ""
        var ISFT: String = ""
    }

    class SampleDataChunk {
        var smpl: ByteBuffer = ByteBuffer.allocate(0)
        var sm24: ByteBuffer = ByteBuffer.allocate(0)
    }

    class PresetDataChunk {
        data class PresetHeader(
                var presetName: String = "",
                // TODO: 04/01/2021 Continue here
        )

    }

    lateinit var infoChunk: InfoChunk
    lateinit var sampleDataChunk: SampleDataChunk
    lateinit var presetDataChunk: PresetDataChunk


}