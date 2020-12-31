package com.github.basshelal.korgpi.sf2

class SF2Layer {

    var name: String = ""
    var globalRegion: SF2Region? = null
    val regions: MutableList<SF2LayerRegion> = mutableListOf()

    override fun toString(): String {
        return "Layer: $name"
    }

}