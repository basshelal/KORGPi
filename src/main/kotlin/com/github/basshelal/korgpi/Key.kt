@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi

import com.github.basshelal.korgpi.Notes.A
import com.github.basshelal.korgpi.Notes.As
import com.github.basshelal.korgpi.Notes.B
import com.github.basshelal.korgpi.Notes.C
import com.github.basshelal.korgpi.Notes.Cs
import com.github.basshelal.korgpi.Notes.D
import com.github.basshelal.korgpi.Notes.Ds
import com.github.basshelal.korgpi.Notes.E
import com.github.basshelal.korgpi.Notes.F
import com.github.basshelal.korgpi.Notes.Fs
import com.github.basshelal.korgpi.Notes.G
import com.github.basshelal.korgpi.Notes.Gs
import com.github.basshelal.korgpi.Octaves.EIGHT
import com.github.basshelal.korgpi.Octaves.FIVE
import com.github.basshelal.korgpi.Octaves.FOUR
import com.github.basshelal.korgpi.Octaves.MINUS
import com.github.basshelal.korgpi.Octaves.NINE
import com.github.basshelal.korgpi.Octaves.ONE
import com.github.basshelal.korgpi.Octaves.SEVEN
import com.github.basshelal.korgpi.Octaves.SIX
import com.github.basshelal.korgpi.Octaves.THREE
import com.github.basshelal.korgpi.Octaves.TWO
import com.github.basshelal.korgpi.Octaves.ZERO
import com.github.basshelal.korgpi.extensions.D
import com.github.basshelal.korgpi.extensions.I
import kotlin.math.log2
import kotlin.math.pow

object MidiKeyMap {

    val keys: List<Key> = listOf(
            // Octave MINUS
            Key(C, MINUS, 0),
            Key(Cs, MINUS, 1),
            Key(D, MINUS, 2),
            Key(Ds, MINUS, 3),
            Key(E, MINUS, 4),
            Key(F, MINUS, 5),
            Key(Fs, MINUS, 6),
            Key(G, MINUS, 7),
            Key(Gs, MINUS, 8),
            Key(A, MINUS, 9),
            Key(As, MINUS, 10),
            Key(B, MINUS, 11),
            // Octave ZERO
            Key(C, ZERO, 12),
            Key(Cs, ZERO, 13),
            Key(D, ZERO, 14),
            Key(Ds, ZERO, 15),
            Key(E, ZERO, 16),
            Key(F, ZERO, 17),
            Key(Fs, ZERO, 18),
            Key(G, ZERO, 19),
            Key(Gs, ZERO, 20),
            Key(A, ZERO, 21),
            Key(As, ZERO, 22),
            Key(B, ZERO, 23),
            // Octave ONE
            Key(C, ONE, 24),
            Key(Cs, ONE, 25),
            Key(D, ONE, 26),
            Key(Ds, ONE, 27),
            Key(E, ONE, 28),
            Key(F, ONE, 29),
            Key(Fs, ONE, 30),
            Key(G, ONE, 31),
            Key(Gs, ONE, 32),
            Key(A, ONE, 33),
            Key(As, ONE, 34),
            Key(B, ONE, 35),
            // Octave TWO
            Key(C, TWO, 36),
            Key(Cs, TWO, 37),
            Key(D, TWO, 38),
            Key(Ds, TWO, 39),
            Key(E, TWO, 40),
            Key(F, TWO, 41),
            Key(Fs, TWO, 42),
            Key(G, TWO, 43),
            Key(Gs, TWO, 44),
            Key(A, TWO, 45),
            Key(As, TWO, 46),
            Key(B, TWO, 47),
            // Octave THREE
            Key(C, THREE, 48),
            Key(Cs, THREE, 49),
            Key(D, THREE, 50),
            Key(Ds, THREE, 51),
            Key(E, THREE, 52),
            Key(F, THREE, 53),
            Key(Fs, THREE, 54),
            Key(G, THREE, 55),
            Key(Gs, THREE, 56),
            Key(A, THREE, 57),
            Key(As, THREE, 58),
            Key(B, THREE, 59),
            // Octave FOUR
            Key(C, FOUR, 60),
            Key(Cs, FOUR, 61),
            Key(D, FOUR, 62),
            Key(Ds, FOUR, 63),
            Key(E, FOUR, 64),
            Key(F, FOUR, 65),
            Key(Fs, FOUR, 66),
            Key(G, FOUR, 67),
            Key(Gs, FOUR, 68),
            Key(A, FOUR, 69),
            Key(As, FOUR, 70),
            Key(B, FOUR, 71),
            // Octave FIVE
            Key(C, FIVE, 72),
            Key(Cs, FIVE, 73),
            Key(D, FIVE, 74),
            Key(Ds, FIVE, 75),
            Key(E, FIVE, 76),
            Key(F, FIVE, 77),
            Key(Fs, FIVE, 78),
            Key(G, FIVE, 79),
            Key(Gs, FIVE, 80),
            Key(A, FIVE, 81),
            Key(As, FIVE, 82),
            Key(B, FIVE, 83),
            // Octave SIX
            Key(C, SIX, 84),
            Key(Cs, SIX, 85),
            Key(D, SIX, 86),
            Key(Ds, SIX, 87),
            Key(E, SIX, 88),
            Key(F, SIX, 89),
            Key(Fs, SIX, 90),
            Key(G, SIX, 91),
            Key(Gs, SIX, 92),
            Key(A, SIX, 93),
            Key(As, SIX, 94),
            Key(B, SIX, 95),
            // Octave SEVEN
            Key(C, SEVEN, 96),
            Key(Cs, SEVEN, 97),
            Key(D, SEVEN, 98),
            Key(Ds, SEVEN, 99),
            Key(E, SEVEN, 100),
            Key(F, SEVEN, 101),
            Key(Fs, SEVEN, 102),
            Key(G, SEVEN, 103),
            Key(Gs, SEVEN, 104),
            Key(A, SEVEN, 105),
            Key(As, SEVEN, 106),
            Key(B, SEVEN, 107),
            // Octave EIGHT
            Key(C, EIGHT, 108),
            Key(Cs, EIGHT, 109),
            Key(D, EIGHT, 110),
            Key(Ds, EIGHT, 111),
            Key(E, EIGHT, 112),
            Key(F, EIGHT, 113),
            Key(Fs, EIGHT, 114),
            Key(G, EIGHT, 115),
            Key(Gs, EIGHT, 116),
            Key(A, EIGHT, 117),
            Key(As, EIGHT, 118),
            Key(B, EIGHT, 119),
            // Octave NINE
            Key(C, NINE, 120),
            Key(Cs, NINE, 121),
            Key(D, NINE, 122),
            Key(Ds, NINE, 123),
            Key(E, NINE, 124),
            Key(F, NINE, 125),
            Key(Fs, NINE, 126),
            Key(G, NINE, 127)
    )

    operator fun get(index: Int): Key = if (index in (0..127)) keys[index] else Key.NULL
}

enum class Notes {
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

enum class Octaves {
    MINUS, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, X
}

data class Key(val note: Notes, val octave: Octaves, val midiNumber: Int) {

    val frequency: Double = 2.0.pow((midiNumber.D - 69.0) / 12.0) * 440.0

    infix fun centsDifferenceFrom(other: Key): Int = (1200 * log2(other.frequency / this.frequency)).I

    infix fun addedCents(cents: Int): Double = this.frequency * (2.0.pow(cents.D / 1200.D))

    companion object {
        val NULL = Key(Notes.X, Octaves.X, -1)

        fun fromMidiNumber(number: Int): Key = MidiKeyMap[number]

        fun fromFrequency(frequency: Double): Key = MidiKeyMap[((12 * log2(frequency / 440.0)) + 69).I]

        fun fromNoteOctave(note: Notes, octave: Octaves): Key =
                MidiKeyMap.keys.find { it.note == note && it.octave == octave } ?: NULL
    }
}