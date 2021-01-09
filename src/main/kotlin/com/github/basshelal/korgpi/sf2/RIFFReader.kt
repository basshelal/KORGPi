package com.github.basshelal.korgpi.sf2

import com.github.basshelal.korgpi.UINT_MAX
import com.github.basshelal.korgpi.extensions.B
import com.github.basshelal.korgpi.extensions.I
import com.github.basshelal.korgpi.extensions.L
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import kotlin.math.min

class RIFFReader(val stream: InputStream) : InputStream(), Iterable<RIFFReader> {

    private val root: RIFFReader
    private var _filePointer: Long = 0L
    private var fourcc: String = ""
    private var riff_type: String? = null
    private var ckSize: Long = 0
    private var avail: Long = UINT_MAX
    private var lastIterator: RIFFReader? = null

    init {
        var invalid = false
        root = if (stream is RIFFReader) stream.root else this

        // Check for RIFF null paddings
        var byte: Int
        while (true) {
            byte = read()
            if (byte == -1) {
                fourcc = ""
                riff_type = null
                ckSize = 0
                avail = 0
                invalid = true
                break
            }
            if (byte != 0) break
        }

        if (!invalid) {
            val rawFourcc = ByteArray(4)
            rawFourcc[0] = byte.B
            readFully(rawFourcc, 1, 3)
            this.fourcc = String(rawFourcc, Charsets.US_ASCII)
            ckSize = readUInt()
            avail = ckSize

            if (format == "RIFF" || format == "LIST") {
                val format = ByteArray(4)
                readFully(format)
                riff_type = String(format, Charsets.US_ASCII)
            }
        }
    }

    val filePointer: Long get() = root.filePointer

    val format: String get() = fourcc

    val type: String? get() = riff_type

    val size: Long get() = ckSize

    val hasNextChunk: Boolean
        get() {
            if (lastIterator != null) lastIterator?.finish()
            return avail != 0L
        }

    val nextChunk: RIFFReader?
        get() {
            lastIterator?.finish()
            if (avail == 0L) return null
            lastIterator = RIFFReader(this)
            return lastIterator
        }

    override fun iterator(): Iterator<RIFFReader> {
        return object : Iterator<RIFFReader> {
            override fun hasNext(): Boolean = hasNextChunk

            override fun next(): RIFFReader = nextChunk!!

        }
    }

    fun readFully(buffer: ByteArray, offset: Int = 0, length: Int = buffer.size) {
        if (length < 0) throw IndexOutOfBoundsException()
        var off = offset
        var len = length
        while (len > 0) {
            val s = read(buffer, off, len)
            if (s < 0) throw EOFException("$s \n $buffer $off $len")
            if (s == 0) Thread.yield()
            off += s
            len -= s
        }
    }

    // Read 8 bit unsigned integer from stream
    fun readUByte(): Int {
        val ch = read()
        if (ch < 0) throw EOFException()
        return ch
    }

    // Read 8 bit signed integer from stream
    fun readByte(): Byte {
        val ch = read()
        if (ch < 0) throw EOFException()
        return ch.B
    }

    // Read 16 bit unsigned integer from stream
    fun readUShort(): Int {
        val ch1 = read()
        val ch2 = read()
        if (ch1 < 0) throw EOFException()
        if (ch2 < 0) throw EOFException()
        return ch1 or (ch2 shl 8)
    }

    // Read 16 bit signed integer from stream
    fun readShort(): Short {
        val ch1 = read()
        val ch2 = read()
        if (ch1 < 0 || ch2 < 0) throw EOFException()
        return (ch1 or (ch2 shl 8)).toShort()
    }

    // Read 32 bit unsigned integer from stream
    fun readUInt(): Long {
        val ch1: Long = read().L
        val ch2: Long = read().L
        val ch3: Long = read().L
        val ch4: Long = read().L
        if (ch1 < 0 || ch2 < 0 || ch3 < 0 || ch4 < 0) throw EOFException()
        return ch1 + (ch2 shl 8) or (ch3 shl 16) or (ch4 shl 24)
    }

    // Read 32 bit signed integer from stream
    fun readInt(): Int {
        val ch1 = read()
        val ch2 = read()
        val ch3 = read()
        val ch4 = read()
        if (ch1 < 0 || ch2 < 0 || ch3 < 0 || ch4 < 0) throw EOFException()
        return ch1 + (ch2 shl 8) or (ch3 shl 16) or (ch4 shl 24)
    }

    // Read ASCII chars from stream
    fun readString(length: Int = available): String {
        val buff: ByteArray
        try {
            buff = ByteArray(length)
        } catch (oom: OutOfMemoryError) {
            throw IOException("Length too big", oom)
        }
        readFully(buff)
        buff.forEachIndexed { index: Int, it: Byte ->
            if (it == 0.B) return String(buff, 0, index, Charsets.US_ASCII)
        }
        return String(buff, Charsets.US_ASCII)
    }

    // Read 64 bit signed integer from stream
    fun readLong(): Long {
        val ch1 = read().toLong()
        val ch2 = read().toLong()
        val ch3 = read().toLong()
        val ch4 = read().toLong()
        val ch5 = read().toLong()
        val ch6 = read().toLong()
        val ch7 = read().toLong()
        val ch8 = read().toLong()
        if (ch1 < 0 || ch2 < 0 || ch3 < 0 || ch4 < 0 ||
                ch5 < 0 || ch6 < 0 || ch7 < 0 || ch8 < 0) throw EOFException()
        return (ch1 or (ch2 shl 8) or (ch3 shl 16) or (ch4 shl 24)
                or (ch5 shl 32) or (ch6 shl 40) or (ch7 shl 48) or (ch8 shl 56))
    }

    override fun read(): Int {
        if (avail == 0L) return -1
        val byte = stream.read()
        if (byte == -1) {
            avail = 0
            return -1
        }
        avail--
        _filePointer++
        return byte
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (avail == 0L) return -1
        if (length > avail) {
            val rlen = stream.read(buffer, offset, avail.I)
            if (rlen != -1) _filePointer += rlen.L
            avail = 0
            return rlen
        } else {
            val ret = stream.read(buffer, offset, length)
            if (ret == -1) {
                avail = 0
                return -1
            }
            avail -= ret.L
            _filePointer += ret.L
            return ret
        }
    }

    override fun skip(n: Long): Long {
        if (n <= 0 || avail == 0L) return 0
        // will not skip more than
        var remaining = min(n, avail)
        while (remaining > 0) {
            // Some input streams like FileInputStream can return more bytes,
            // when EOF is reached.
            var ret = min(stream.skip(remaining), remaining)
            if (ret == 0L) {
                // EOF or not? we need to check.
                Thread.yield()
                if (stream.read() == -1) {
                    avail = 0
                    break
                }
                ret = 1
            } else if (ret < 0) {
                // the skip should not return negative value, but check it also
                avail = 0
                break
            }
            remaining -= ret
            avail -= ret
            _filePointer += ret
        }
        return n - remaining
    }

    inline val available: Int get() = this.available()

    override fun available(): Int = if (avail > Int.MAX_VALUE) Int.MAX_VALUE else avail.I

    fun finish() {
        if (avail != 0L) skip(avail)
    }

    override fun close() {
        finish()
        stream.close()
    }
}