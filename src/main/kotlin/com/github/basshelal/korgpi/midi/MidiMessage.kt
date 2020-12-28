@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.midi

import com.github.basshelal.korgpi.extensions.F
import com.github.basshelal.korgpi.extensions.I
import org.jaudiolibs.jnajack.JackMidi
import javax.sound.midi.MidiMessage

class MidiMessage(var bytes: UByteArray = UByteArray(3)) {

    var midiEventBuffer = ByteArray(0)

    inline val length: Int get() = bytes.size

    inline fun setData(byteArray: ByteArray) {
        this.bytes = byteArray.toUByteArray()
    }

    inline fun setData(event: JackMidi.Event) {
        event.size().also { size: Int ->
            if (midiEventBuffer.size < size) midiEventBuffer = ByteArray(size)
        }
        event.read(midiEventBuffer)
        this.bytes = midiEventBuffer.toUByteArray()
    }

    inline val command: UByte get() = this.bytes[0]
    inline val data1: UByte get() = this.bytes[1]
    inline val data2: UByte get() = this.bytes[2]

    val pitchBendValue: Float
        get() {
            if (this.command != PITCH_BEND) return 0F
            else {
                // logD((message.data2.toInt() shl 7) or message.data1.toInt())
                // between -1F (left most) and 1F (right most)
                val midiValue = (data2.I shl 7) or data1.I
                assert(midiValue in (PITCH_BEND_MIN..PITCH_BEND_MAX))
                return convertScale(midiValue)
            }
        }

    private fun convertScale(oldValueToConvert: Int): Float {
        val oldScaleMin: Int = PITCH_BEND_MIN
        val oldScaleMax: Int = PITCH_BEND_MAX
        val oldScaleRange: Int = oldScaleMax - oldScaleMin

        val newScaleMin: Float = -1.0F
        val newScaleMax: Float = 1.0F
        val newScaleRange: Float = newScaleMax - newScaleMin

        return ((oldValueToConvert - oldScaleMin).F * newScaleRange.F / oldScaleRange.F) + newScaleMin.F
    }

    override fun toString(): String {
        return this.bytes.joinToString()
    }

    companion object {
        // System common messages

        // Status byte defines
        // System common messages

        /**
         * Status byte for MIDI Time Code Quarter Frame message (0xF1, or 241).
         *
         * @see MidiMessage.getStatus
         */
        const val MIDI_TIME_CODE: UByte = 241U

        /**
         * Status byte for Song Position Pointer message (0xF2, or 242).
         *
         * @see MidiMessage.getStatus
         */
        const val SONG_POSITION_POINTER: UByte = 242U

        /**
         * Status byte for MIDI Song Select message (0xF3, or 243).
         *
         * @see MidiMessage.getStatus
         */
        const val SONG_SELECT: UByte = 243U

        /**
         * Status byte for Tune Request message (0xF6, or 246).
         *
         * @see MidiMessage.getStatus
         */
        const val TUNE_REQUEST: UByte = 246U

        /**
         * Status byte for End of System Exclusive message (0xF7, or 247).
         *
         * @see MidiMessage.getStatus
         */
        const val END_OF_EXCLUSIVE: UByte = 247U

        // System real-time messages

        // System real-time messages

        /**
         * Status byte for Timing Clock message (0xF8, or 248).
         *
         * @see MidiMessage.getStatus
         */
        const val TIMING_CLOCK: UByte = 248U

        /**
         * Status byte for Start message (0xFA, or 250).
         *
         * @see MidiMessage.getStatus
         */
        const val START: UByte = 250U

        /**
         * Status byte for Continue message (0xFB, or 251).
         *
         * @see MidiMessage.getStatus
         */
        const val CONTINUE: UByte = 251U

        /**
         * Status byte for Stop message (0xFC, or 252).
         *
         * @see MidiMessage.getStatus
         */
        const val STOP: UByte = 252U

        /**
         * Status byte for Active Sensing message (0xFE, or 254).
         *
         * @see MidiMessage.getStatus
         */
        const val ACTIVE_SENSING: UByte = 254U

        /**
         * Status byte for System Reset message (0xFF, or 255).
         *
         * @see MidiMessage.getStatus
         */
        const val SYSTEM_RESET: UByte = 255U

        // Channel voice message upper nibble defines

        // Channel voice message upper nibble defines

        /**
         * Command value for Note Off message (0x80, or 128).
         */
        const val NOTE_OFF: UByte = 128U

        /**
         * Command value for Note On message (0x90, or 144).
         */
        const val NOTE_ON: UByte = 144U

        /**
         * Command value for Polyphonic Key Pressure (Aftertouch) message (0xA0, or
         * 160).
         */
        const val POLY_PRESSURE: UByte = 160U

        /**
         * Command value for Control Change message (0xB0, or 176).
         */
        const val CONTROL_CHANGE: UByte = 176U

        /**
         * Command value for Program Change message (0xC0, or 192).
         */
        const val PROGRAM_CHANGE: UByte = 192U

        /**
         * Command value for Channel Pressure (Aftertouch) message (0xD0, or 208).
         */
        const val CHANNEL_PRESSURE: UByte = 208U

        /**
         * Command value for Pitch Bend message (0xE0, or 224).
         */
        const val PITCH_BEND: UByte = 224U


        // Some Constants

        const val PITCH_BEND_CENTER: Int = 0x2000 // 8192
        const val PITCH_BEND_MAX: Int = 0x3FFF // 16383
        const val PITCH_BEND_MIN: Int = 0x0000 // 0

        const val MAX_NOTES: Int = 128
    }

}