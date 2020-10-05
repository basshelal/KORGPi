@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi

import com.github.basshelal.korgpi.Note.A
import com.github.basshelal.korgpi.Note.As
import com.github.basshelal.korgpi.Note.B
import com.github.basshelal.korgpi.Note.C
import com.github.basshelal.korgpi.Note.Cs
import com.github.basshelal.korgpi.Note.D
import com.github.basshelal.korgpi.Note.Ds
import com.github.basshelal.korgpi.Note.E
import com.github.basshelal.korgpi.Note.F
import com.github.basshelal.korgpi.Note.Fs
import com.github.basshelal.korgpi.Note.G
import com.github.basshelal.korgpi.Note.Gs
import com.github.basshelal.korgpi.Octave.EIGHT
import com.github.basshelal.korgpi.Octave.FIVE
import com.github.basshelal.korgpi.Octave.FOUR
import com.github.basshelal.korgpi.Octave.ONE
import com.github.basshelal.korgpi.Octave.SEVEN
import com.github.basshelal.korgpi.Octave.SIX
import com.github.basshelal.korgpi.Octave.THREE
import com.github.basshelal.korgpi.Octave.TWO
import com.github.basshelal.korgpi.Octave.ZERO
import com.github.basshelal.korgpi.extensions.D
import com.github.basshelal.korgpi.extensions.I
import kotlin.math.log2
import kotlin.math.pow

val MIDI_TO_KEY_MAP = mapOf(
        // Octave ZERO
        12 to Key(C, ZERO),
        13 to Key(Cs, ZERO),
        14 to Key(D, ZERO),
        15 to Key(Ds, ZERO),
        16 to Key(E, ZERO),
        17 to Key(F, ZERO),
        18 to Key(Fs, ZERO),
        19 to Key(G, ZERO),
        20 to Key(Gs, ZERO),
        21 to Key(A, ZERO),
        22 to Key(As, ZERO),
        23 to Key(B, ZERO),
        // Octave ONE
        24 to Key(C, ONE),
        25 to Key(Cs, ONE),
        26 to Key(D, ONE),
        27 to Key(Ds, ONE),
        28 to Key(E, ONE),
        29 to Key(F, ONE),
        30 to Key(Fs, ONE),
        31 to Key(G, ONE),
        32 to Key(Gs, ONE),
        33 to Key(A, ONE),
        34 to Key(As, ONE),
        35 to Key(B, ONE),
        // Octave TWO
        36 to Key(C, TWO),
        37 to Key(Cs, TWO),
        38 to Key(D, TWO),
        39 to Key(Ds, TWO),
        40 to Key(E, TWO),
        41 to Key(F, TWO),
        42 to Key(Fs, TWO),
        43 to Key(G, TWO),
        44 to Key(Gs, TWO),
        45 to Key(A, TWO),
        46 to Key(As, TWO),
        47 to Key(B, TWO),
        // Octave THREE
        48 to Key(C, THREE),
        49 to Key(Cs, THREE),
        50 to Key(D, THREE),
        51 to Key(Ds, THREE),
        52 to Key(E, THREE),
        53 to Key(F, THREE),
        54 to Key(Fs, THREE),
        55 to Key(G, THREE),
        56 to Key(Gs, THREE),
        57 to Key(A, THREE),
        58 to Key(As, THREE),
        59 to Key(B, THREE),
        // Octave FOUR
        60 to Key(C, FOUR),
        61 to Key(Cs, FOUR),
        62 to Key(D, FOUR),
        63 to Key(Ds, FOUR),
        64 to Key(E, FOUR),
        65 to Key(F, FOUR),
        66 to Key(Fs, FOUR),
        67 to Key(G, FOUR),
        68 to Key(Gs, FOUR),
        69 to Key(A, FOUR),
        70 to Key(As, FOUR),
        71 to Key(B, FOUR),
        // Octave FIVE
        72 to Key(C, FIVE),
        73 to Key(Cs, FIVE),
        74 to Key(D, FIVE),
        75 to Key(Ds, FIVE),
        76 to Key(E, FIVE),
        77 to Key(F, FIVE),
        78 to Key(Fs, FIVE),
        79 to Key(G, FIVE),
        80 to Key(Gs, FIVE),
        81 to Key(A, FIVE),
        82 to Key(As, FIVE),
        83 to Key(B, FIVE),
        // Octave SIX
        84 to Key(C, SIX),
        85 to Key(Cs, SIX),
        86 to Key(D, SIX),
        87 to Key(Ds, SIX),
        88 to Key(E, SIX),
        89 to Key(F, SIX),
        90 to Key(Fs, SIX),
        91 to Key(G, SIX),
        92 to Key(Gs, SIX),
        93 to Key(A, SIX),
        94 to Key(As, SIX),
        95 to Key(B, SIX),
        // Octave SEVEN
        96 to Key(C, SEVEN),
        97 to Key(Cs, SEVEN),
        98 to Key(D, SEVEN),
        99 to Key(Ds, SEVEN),
        100 to Key(E, SEVEN),
        101 to Key(F, SEVEN),
        102 to Key(Fs, SEVEN),
        103 to Key(G, SEVEN),
        104 to Key(Gs, SEVEN),
        105 to Key(A, SEVEN),
        106 to Key(As, SEVEN),
        107 to Key(B, SEVEN),
        // Octave EIGHT
        108 to Key(C, EIGHT),
        109 to Key(Cs, EIGHT),
        110 to Key(D, EIGHT),
        111 to Key(Ds, EIGHT),
        112 to Key(E, EIGHT),
        113 to Key(F, EIGHT),
        114 to Key(Fs, EIGHT),
        115 to Key(G, EIGHT),
        116 to Key(Gs, EIGHT),
        117 to Key(A, EIGHT),
        118 to Key(As, EIGHT),
        119 to Key(B, EIGHT)
)

enum class Note {
    C,
    Cs,
    D,
    Ds,
    E,
    F,
    Fs,
    G,
    Gs,
    A,
    As,
    B,
    X
}

enum class Octave {
    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NONE
}

data class Key(val note: Note, val octave: Octave) {

    val number: Int
        get() = getNumber(this)

    val frequency: Double
        get() {
            val exp = (number.D - 69.0) / 12.0
            return 2.0.pow(exp) * 440.0
        }

    infix fun centsDifferenceFrom(other: Key) = centsDifference(this, other)

    infix fun addCents(cents: Int) = addCents(this, cents)

    companion object {
        fun fromNumber(number: Int): Key {
            return getKey(number)
        }

        fun fromFrequency(frequency: Double): Key {
            val number = (12 * log2(frequency / 440.0)) + 69
            return fromNumber(number.I)
        }

        fun centsDifference(from: Key, to: Key): Int {
            return (1200 * log2(to.frequency / from.frequency)).I
        }

        fun addCents(from: Key, cents: Int): Double {
            return from.frequency * (2.0.pow(cents.D / 1200.D))
        }
    }
}

inline fun getKey(number: Int): Key {
    require(number in (12..119))
    return MIDI_TO_KEY_MAP[number]!!
}

inline fun getNumber(key: Key): Int {
    val results = MIDI_TO_KEY_MAP.filter { it.value == key }.keys
    require(results.size == 1)
    return results.first()
}