package com.github.basshelal.korgpi.sf2

data class SF2Modulator(
        var sourceOperator: Int = 0,
        var destinationOperator: Int = 0,
        var amount: Short = 0,
        var amountSourceOperator: Int = 0,
        var transportOperator: Int = 0) {

    companion object {

        const val SOURCE_NONE = 0

        const val SOURCE_NOTE_ON_VELOCITY = 2

        const val SOURCE_NOTE_ON_KEYNUMBER = 3

        const val SOURCE_POLY_PRESSURE = 10

        const val SOURCE_CHANNEL_PRESSURE = 13

        const val SOURCE_PITCH_WHEEL = 14

        const val SOURCE_PITCH_SENSITIVITY = 16

        const val SOURCE_MIDI_CONTROL = 128 * 1

        const val SOURCE_DIRECTION_MIN_MAX = 256 * 0

        const val SOURCE_DIRECTION_MAX_MIN = 256 * 1

        const val SOURCE_POLARITY_UNIPOLAR = 512 * 0

        const val SOURCE_POLARITY_BIPOLAR = 512 * 1

        const val SOURCE_TYPE_LINEAR = 1024 * 0

        const val SOURCE_TYPE_CONCAVE = 1024 * 1

        const val SOURCE_TYPE_CONVEX = 1024 * 2

        const val SOURCE_TYPE_SWITCH = 1024 * 3

        const val TRANSFORM_LINEAR = 0

        const val TRANSFORM_ABSOLUTE = 2

    }
}