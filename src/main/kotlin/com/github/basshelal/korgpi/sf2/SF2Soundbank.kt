package com.github.basshelal.korgpi.sf2

import com.github.basshelal.korgpi.extensions.L
import com.sun.media.sound.ModelByteBuffer
import com.sun.media.sound.SF2Instrument
import com.sun.media.sound.SF2Layer
import com.sun.media.sound.SF2Sample
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.ArrayList

class SF2Soundbank(inputStream: InputStream) {

    // version of the Sound Font RIFF file
    var major = 2
    var minor = 1

    // target Sound Engine
    var targetEngine = "EMU8000"

    // Sound Font Bank Name
    var name = "untitled"

    // Sound ROM Name
    var romName: String? = null

    // Sound ROM Version
    var romVersionMajor = -1
    var romVersionMinor = -1

    // Date of Creation of the Bank
    var creationDate: String? = null

    // Sound Designers and Engineers for the Bank
    var engineers: String? = null

    // Product for which the Bank was intended
    var product: String? = null

    // Copyright message
    var copyright: String? = null

    // Comments
    var comments: String? = null

    // The SoundFont tools used to create and alter the bank
    var tools: String? = null

    // The Sample Data loaded from the SoundFont
    // TODO: 31/12/2020 Reimplement types
    private var sampleData: ModelByteBuffer? = null
    private var sampleData24: ModelByteBuffer? = null
    private val sampleFile: File? = null
    private val largeFormat = false
    private val instruments: List<SF2Instrument> = ArrayList()
    private val layers: List<SF2Layer> = ArrayList()
    private val samples: List<SF2Sample> = ArrayList()

    constructor(file: File) : this(kotlin.run {
        val inputStream: InputStream = FileInputStream(file)
        return@run inputStream
    })

    constructor(url: URL) : this(kotlin.run {
        val inputStream: InputStream = url.openStream()
        return@run inputStream
    })

    init {
        try {
            // readSoundbank
            val riffReader = RIFFReader(inputStream)
            if (riffReader.format != "RIFF") {
                throw RIFFInvalidFormatException("Input stream is not a valid RIFF stream!")
            }
            if (riffReader.type != "sfbk") {
                throw RIFFInvalidFormatException("Input stream is not a valid SoundFont!")
            }
            while (riffReader.hasNextChunk) {
                val chunk = riffReader.nextChunk
                if (chunk?.format == "LIST") {
                    if (chunk.type == "INFO") readInfoChunk(chunk)
                    if (chunk.type == "sdta") readSdtaChunk(chunk)
                    if (chunk.type == "pdta") readPdtaChunk(chunk)
                }
            }
        } catch (io: IOException) {
            io.printStackTrace()
        } finally {
            inputStream.close()
        }
    }

    fun readInfoChunk(riffReader: RIFFReader) {
        while (riffReader.hasNextChunk) {
            val chunk: RIFFReader? = riffReader.nextChunk
            val format = chunk?.format
            when (format) {
                "ifil" -> {
                    this.major = chunk.readUnsignedShort()
                    this.minor = chunk.readUnsignedShort()
                }
                "isng" -> {
                    this.targetEngine = chunk.readString(chunk.available())
                }
                "INAM" -> {
                    this.name = chunk.readString(chunk.available())
                }
                "irom" -> {
                    this.romName = chunk.readString(chunk.available())
                }
                "iver" -> {
                    this.romVersionMajor = chunk.readUnsignedShort()
                    this.romVersionMinor = chunk.readUnsignedShort()
                }
                "ICRD" -> {
                    this.creationDate = chunk.readString(chunk.available())
                }
                "IENG" -> {
                    this.engineers = chunk.readString(chunk.available())
                }
                "IPRD" -> {
                    this.product = chunk.readString(chunk.available())
                }
                "ICOP" -> {
                    this.copyright = chunk.readString(chunk.available())
                }
                "ICMT" -> {
                    this.comments = chunk.readString(chunk.available())
                }
                "ISFT" -> {
                    this.tools = chunk.readString(chunk.available())
                }
            }
        }
    }

    fun readSdtaChunk(riffReader: RIFFReader) {
        while (riffReader.hasNextChunk) {
            val chunk: RIFFReader? = riffReader.nextChunk
            if (chunk?.format == "smpl") {
                if (!largeFormat) {
                    val sampleData = ByteArray(chunk.available())
                    var read = 0
                    val avail = chunk.available()
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
                    //chunk.read(sampleData);
                } else {
                    sampleData = ModelByteBuffer(sampleFile,
                            chunk.filePointer, chunk.available().L)
                }
            }
            if (chunk?.format == "sm24") {
                if (!largeFormat) {
                    val sampleData24 = ByteArray(chunk.available())
                    //chunk.read(sampleData24);
                    var read = 0
                    val avail = chunk.available()
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
                } else {
                    sampleData24 = ModelByteBuffer(sampleFile,
                            chunk.filePointer, chunk.available().L)
                }
            }
        }
    }

    fun readPdtaChunk(riffReader: RIFFReader) {
        TODO()
    }

}