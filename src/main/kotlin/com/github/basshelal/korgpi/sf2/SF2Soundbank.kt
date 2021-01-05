@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE", "NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.sf2

import com.github.basshelal.korgpi.extensions.I
import com.sun.media.sound.ModelByteBuffer
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL

// TODO: 31/12/2020 Reimplement this, probably into a java.nio.ByteBuffer
typealias MByteBuffer = ModelByteBuffer

class SF2Soundbank(inputStream: InputStream) {

    // version of the Sound Font RIFF file
    var major: Int = -1
    var minor: Int = -1

    // target Sound Engine
    var targetEngine: String = "EMU8000"

    // Sound Font Bank Name
    var name: String = "untitled"

    // Sound ROM Name
    var romName: String = ""

    // Sound ROM Version
    var romVersionMajor: Int = -1
    var romVersionMinor: Int = -1

    // Date of Creation of the Bank
    var creationDate: String = ""

    // Sound Designers and Engineers for the Bank
    var engineers: String = ""

    // Product for which the Bank was intended
    var product: String = ""

    // Copyright message
    var copyright: String = ""

    // Comments
    var comments: String = ""

    // The SoundFont tools used to create and alter the bank
    var tools: String = ""

    // The Sample Data loaded from the SoundFont
    var sampleData: MByteBuffer? = null
    var sampleData24: MByteBuffer? = null

    val instruments: MutableList<SF2Instrument> = mutableListOf()
    val layers: MutableList<SF2Layer> = mutableListOf()
    val samples: MutableList<SF2Sample> = mutableListOf()

    constructor(file: File) : this(FileInputStream(file))

    constructor(url: URL) : this(url.openStream())

    constructor(filePath: String) : this(FileInputStream(filePath))

    init {
        try {
            val riffReader = RIFFReader(inputStream)
            if (riffReader.format != "RIFF") throw RIFFInvalidFormatException("Input stream is not a valid RIFF stream!")
            if (riffReader.type != "sfbk") throw RIFFInvalidFormatException("Input stream is not a valid SoundFont!")

            riffReader.forEach { chunk ->
                if (chunk.format == "LIST") {
                    if (chunk.type == "INFO") readInfoChunk(chunk)
                    if (chunk.type == "sdta") readSdtaChunk(chunk)
                    if (chunk.type == "pdta") readPdtaChunk(chunk)
                }
            }
        } finally {
            inputStream.close()
        }
    }

    private inline fun readInfoChunk(riffReader: RIFFReader) {
        riffReader.forEach { chunk ->
            when (chunk.format) {
                "ifil" -> {
                    this.major = chunk.readUnsignedShort()
                    this.minor = chunk.readUnsignedShort()
                }
                "isng" -> {
                    this.targetEngine = chunk.readString(chunk.available)
                }
                "INAM" -> {
                    this.name = chunk.readString(chunk.available)
                }
                "irom" -> {
                    this.romName = chunk.readString(chunk.available)
                }
                "iver" -> {
                    this.romVersionMajor = chunk.readUnsignedShort()
                    this.romVersionMinor = chunk.readUnsignedShort()
                }
                "ICRD" -> {
                    this.creationDate = chunk.readString(chunk.available)
                }
                "IENG" -> {
                    this.engineers = chunk.readString(chunk.available)
                }
                "IPRD" -> {
                    this.product = chunk.readString(chunk.available)
                }
                "ICOP" -> {
                    this.copyright = chunk.readString(chunk.available)
                }
                "ICMT" -> {
                    this.comments = chunk.readString(chunk.available)
                }
                "ISFT" -> {
                    this.tools = chunk.readString(chunk.available)
                }
            }
        }
        // TODO: 05/01/2021 Verify all necessary was loaded, else throw an Exception
    }

    private inline fun readSdtaChunk(riffReader: RIFFReader) {
        riffReader.forEach { chunk ->
            when (chunk.format) {
                "smpl" -> {
                    val sampleData = ByteArray(chunk.available)
                    var read = 0
                    val avail = chunk.available
                    while (read != avail) {
                        if (avail - read > 65536) {
                            chunk.readFully(sampleData, read, 65536)
                            read += 65536
                        } else {
                            chunk.readFully(sampleData, read, avail - read)
                            read = avail
                        }
                    }
                    this.sampleData = ModelByteBuffer(sampleData)
                }
                "sm24" -> {
                    val sampleData24 = ByteArray(chunk.available)
                    var read = 0
                    val avail = chunk.available
                    while (read != avail) {
                        if (avail - read > 65536) {
                            chunk.readFully(sampleData24, read, 65536)
                            read += 65536
                        } else {
                            chunk.readFully(sampleData24, read, avail - read)
                            read = avail
                        }
                    }
                    this.sampleData24 = ModelByteBuffer(sampleData24)
                }
            }
        }
    }

    private inline fun readPdtaChunk(riffReader: RIFFReader) {
        val presets = mutableListOf<SF2Instrument>()
        val presets_bagNdx = mutableListOf<Int>()
        val presets_splits_gen = mutableListOf<SF2InstrumentRegion?>()
        val presets_splits_mod = mutableListOf<SF2InstrumentRegion?>()

        val instruments = mutableListOf<SF2Layer>()
        val instruments_bagNdx = mutableListOf<Int>()
        val instruments_splits_gen = mutableListOf<SF2LayerRegion?>()
        val instruments_splits_mod = mutableListOf<SF2LayerRegion?>()

        riffReader.forEach { chunk: RIFFReader ->
            when (chunk.format) {
                "phdr" -> {
                    // Preset Header / Instrument
                    if (chunk.available() % 38 != 0) throw RIFFInvalidDataException("RIFF Invalid Data")
                    val count: Int = chunk.available() / 38
                    for (i in 0 until count) {
                        SF2Instrument(/*this*/).also { preset ->
                            preset.name = chunk.readString(20)
                            preset.preset = chunk.readUnsignedShort()
                            preset.bank = chunk.readUnsignedShort()
                            presets_bagNdx.add(chunk.readUnsignedShort())
                            preset.library = chunk.readUnsignedInt()
                            preset.genre = chunk.readUnsignedInt()
                            preset.morphology = chunk.readUnsignedInt()
                            presets.add(preset)
                            if (i != count - 1) this.instruments.add(preset)
                        }
                    }
                }
                "pbag" -> {
                    // Preset Zones / Instrument splits
                    if (chunk.available() % 4 != 0) throw RIFFInvalidDataException("RIFF Invalid Data")
                    var count: Int = chunk.available() / 4

                    // Skip first record
                    kotlin.run {
                        val gencount = chunk.readUnsignedShort()
                        val modcount = chunk.readUnsignedShort()
                        while (presets_splits_gen.size < gencount) presets_splits_gen.add(null)
                        while (presets_splits_mod.size < modcount) presets_splits_mod.add(null)
                        count--
                    }

                    if (presets_bagNdx.isEmpty()) throw RIFFInvalidDataException("RIFF Invalid Data")

                    val offset = presets_bagNdx.first()
                    // Offset should be 0 (but just in case)
                    for (i in 0 until offset) {
                        if (count == 0) throw RIFFInvalidDataException("RIFF Invalid Data")

                        val gencount = chunk.readUnsignedShort()
                        val modcount = chunk.readUnsignedShort()
                        while (presets_splits_gen.size < gencount) presets_splits_gen.add(null)
                        while (presets_splits_mod.size < modcount) presets_splits_mod.add(null)
                        count--
                    }

                    for (i in 0 until presets_bagNdx.size - 1) {
                        val zone_count = presets_bagNdx[i + 1] - presets_bagNdx[i]
                        val preset = presets[i]
                        for (ii in 0 until zone_count) {
                            if (count == 0) throw RIFFInvalidDataException("RIFF Invalid Data")
                            val gencount = chunk.readUnsignedShort()
                            val modcount = chunk.readUnsignedShort()
                            val split = SF2InstrumentRegion()
                            preset.regions.add(split)
                            while (presets_splits_gen.size < gencount) presets_splits_gen.add(split)
                            while (presets_splits_mod.size < modcount) presets_splits_mod.add(split)
                            count--
                        }
                    }
                }
                "pmod" -> {
                    // Preset Modulators / Split Modulators
                    for (i in 0 until presets_splits_mod.size) {
                        val modulator = SF2Modulator()
                        modulator.sourceOperator = chunk.readUnsignedShort()
                        modulator.destinationOperator = chunk.readUnsignedShort()
                        modulator.amount = chunk.readShort()
                        modulator.amountSourceOperator = chunk.readUnsignedShort()
                        modulator.transportOperator = chunk.readUnsignedShort()
                        val split = presets_splits_mod[i]
                        if (split != null) split.modulators.add(modulator)
                    }
                }
                "pgen" -> {
                    // Preset Generators / Split Generators
                    for (i in 0 until presets_splits_gen.size) {
                        val operator = chunk.readUnsignedShort()
                        val amount = chunk.readShort()
                        val split = presets_splits_gen[i]
                        if (split != null) split.generators[operator] = amount
                    }
                }
                "inst" -> {
                    // Instrument Header / Layers
                    if (chunk.available() % 22 != 0) throw RIFFInvalidDataException("RIFF Invalid Data")
                    val count = chunk.available() / 22
                    for (i in 0 until count) {
                        val layer = SF2Layer(/*this*/)
                        layer.name = chunk.readString(20)
                        instruments_bagNdx.add(chunk.readUnsignedShort())
                        instruments.add(layer)
                        if (i != count - 1) this.layers.add(layer)
                    }
                }
                "ibag" -> {
                    // Instrument Zones / Layer splits
                    if (chunk.available() % 4 != 0) throw RIFFInvalidDataException("RIFF Invalid Data")
                    var count = chunk.available() / 4

                    // Skip first record
                    kotlin.run {
                        val gencount = chunk.readUnsignedShort()
                        val modcount = chunk.readUnsignedShort()
                        while (instruments_splits_gen.size < gencount) instruments_splits_gen.add(null)
                        while (instruments_splits_mod.size < modcount) instruments_splits_mod.add(null)
                        count--
                    }

                    if (instruments_bagNdx.isEmpty()) throw RIFFInvalidDataException("RIFF Invalid Data")

                    val offset = instruments_bagNdx.first()
                    // Offset should be 0 but (just in case)
                    for (i in 0 until offset) {
                        if (count == 0) throw RIFFInvalidDataException("RIFF Invalid Data")
                        val gencount = chunk.readUnsignedShort()
                        val modcount = chunk.readUnsignedShort()
                        while (instruments_splits_gen.size < gencount) instruments_splits_gen.add(null)
                        while (instruments_splits_mod.size < modcount) instruments_splits_mod.add(null)
                        count--
                    }

                    for (i in 0 until instruments_bagNdx.size - 1) {
                        val zone_count = instruments_bagNdx[i + 1] - instruments_bagNdx[i]
                        val layer = layers[i]
                        for (ii in 0 until zone_count) {
                            if (count == 0) throw RIFFInvalidDataException("RIFF Invalid Data")
                            val gencount = chunk.readUnsignedShort()
                            val modcount = chunk.readUnsignedShort()
                            val split = SF2LayerRegion()
                            layer.regions.add(split)
                            while (instruments_splits_gen.size < gencount) instruments_splits_gen.add(split)
                            while (instruments_splits_mod.size < modcount) instruments_splits_mod.add(split)
                            count--
                        }
                    }
                }
                "imod" -> {
                    // Instrument Modulators / Split Modulators
                    for (i in 0 until instruments_splits_mod.size) {
                        val modulator = SF2Modulator()
                        modulator.sourceOperator = chunk.readUnsignedShort()
                        modulator.destinationOperator = chunk.readUnsignedShort()
                        modulator.amount = chunk.readShort()
                        modulator.amountSourceOperator = chunk.readUnsignedShort()
                        modulator.transportOperator = chunk.readUnsignedShort()
                        if (i < 0 || i >= instruments_splits_gen.size) throw RIFFInvalidDataException("RIFF Invalid Data")
                        val split = instruments_splits_gen[i]
                        if (split != null) split.modulators.add(modulator)
                    }
                }
                "igen" -> {
                    // Instrument Generators / Split Generators
                    for (i in 0 until instruments_splits_gen.size) {
                        val operator = chunk.readUnsignedShort()
                        val amount = chunk.readShort()
                        val split = instruments_splits_gen[i]
                        if (split != null) split.generators[operator] = amount
                    }
                }
                "shdr" -> {
                    // Sample Headers
                    if (chunk.available() % 46 != 0) throw RIFFInvalidDataException("RIFF Invalid Data")
                    val count = chunk.available() / 46
                    for (i in 0 until count) {
                        val sample = SF2Sample(/*this*/)
                        sample.name = chunk.readString(20)
                        val start = chunk.readUnsignedInt()
                        val end = chunk.readUnsignedInt()
                        if (sampleData != null) sample.data = sampleData?.subbuffer(start * 2, end * 2, true)
                        if (sampleData24 != null) sample.data24 = sampleData24?.subbuffer(start, end, true)
                        sample.startLoop = chunk.readUnsignedInt() - start
                        sample.endLoop = chunk.readUnsignedInt() - start
                        if (sample.startLoop < 0) sample.startLoop = -1
                        if (sample.endLoop < 0) sample.endLoop = -1
                        sample.sampleRate = chunk.readUnsignedInt()
                        sample.originalPitch = chunk.readUnsignedByte()
                        sample.pitchCorrection = chunk.readByte()
                        sample.sampleLink = chunk.readUnsignedShort()
                        sample.sampleType = chunk.readUnsignedShort()
                        if (i != count - 1) this.samples.add(sample)
                    }
                }
            }
        }

        this.layers.forEach { layer ->
            var globalSplit: SF2Region? = null
            layer.regions.forEach { split ->
                val sampleid = split.generators[SF2Region.GENERATOR_SAMPLEID]?.I
                if (sampleid != null) {
                    split.generators.remove(SF2Region.GENERATOR_SAMPLEID)
                    if (sampleid < 0 || sampleid >= samples.size) throw RIFFInvalidDataException("RIFF Invalid Data")
                    split.sample = samples[sampleid]
                } else {
                    globalSplit = split
                }
            }
            globalSplit?.also {
                layer.regions.remove(it)
                val gsplit = SF2Region()
                gsplit.generators = it.generators
                gsplit.modulators = it.modulators
                layer.globalRegion = gsplit
            }
        }

        this.instruments.forEach { instrument ->
            var globalSplit: SF2Region? = null
            instrument.regions.forEach { split ->
                val instrumentId = split.generators[SF2Region.GENERATOR_INSTRUMENT]?.I
                if (instrumentId != null) {
                    split.generators.remove(SF2Region.GENERATOR_INSTRUMENT)
                    if (instrumentId < 0 || instrumentId >= layers.size) throw RIFFInvalidDataException("RIFF Invalid Data")
                    split.layer = layers[instrumentId]
                } else {
                    globalSplit = split
                }
            }

            globalSplit?.also {
                instrument.regions.remove(it)
                val gsplit = SF2Region()
                gsplit.generators = it.generators
                gsplit.modulators = it.modulators
                instrument.globalRegion = gsplit
            }
        }

    }

    // TODO: 02/01/2021 Missing SF2 Writing and editing functions

}