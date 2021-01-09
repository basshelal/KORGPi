package com.github.basshelal.korgpi.sf2

import javax.sound.midi.Patch

class SF2Instrument {

    var name: String = ""
    var preset: Int = 0
    var bank: Int = 0
    var index: Int = 0
    var library: Long = 0L
    var genre: Long = 0L
    var morphology: Long = 0L
    var globalRegion: SF2Region? = null
    val regions: MutableList<SF2InstrumentRegion> = mutableListOf()

    val patch: Patch
        get() = if (bank == 128) Patch(0, preset) else Patch(bank shl 7, preset)


    override fun toString(): String {
        return if (bank == 128) "Drumkit: $name preset # $preset"
        else "Instrument: $name bank # $bank preset # $preset"
    }

    fun getPerformers() {
        TODO()
    }
}