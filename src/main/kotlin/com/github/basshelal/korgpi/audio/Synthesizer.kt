package com.github.basshelal.korgpi.audio

// JUCE Synthesizer source here: 
// https://github.com/juce-framework/JUCE/tree/master/modules/juce_audio_basics/synthesisers

/**
 * Base class for a musical device that can play sounds.
 * To create a synthesiser, you'll need to create a subclass of SynthesizerSound
 * to describe each sound available to your synth, and a subclass of SynthesizerVoice
 * which can play back one of these sounds.
 * Then you can use the addVoice() and addSound() methods to give the synthesiser a
 * set of sounds, and a set of voices it can use to play them. If you only give it
 * one voice it will be monophonic - the more voices it has, the more polyphony it'll
 * have available.
 * Then repeatedly call the renderNextBlock() method to produce the audio. Any midi
 * events that go in will be scanned for note on/off messages, and these are used to
 * start and stop the voices playing the appropriate sounds.
 * While it's playing, you can also cause notes to be triggered by calling the noteOn(),
 * noteOff() and other controller methods.
 * Before rendering, be sure to call the setCurrentPlaybackSampleRate() to tell it
 * what the target playback rate is. This value is passed on to the voices so that
 * they can pitch their output correctly.
 */
// TODO: 17-Sep-20 voices needs to be thread-safe because callers can modify whilst we are reading
//  for audio playback
class Synthesizer(private val voices: List<SynthesizerVoice> = mutableListOf()) {

}

/**
 * Represents a voice that a Synthesizer can use to play a SynthesizerSound.
 * A voice plays a single sound at a time, and a synthesiser holds an array of
 * voices so that it can play polyphonically.
 * @see Synthesizer
 * @see SynthesizerSound
 */
class SynthesizerVoice(val sound: SynthesizerSound = SynthesizerSound.EMPTY) {

}


/**
 * Describes one of the sounds that a Synthesizer can play.
 * A synthesizer can contain one or more sounds, and a sound can choose which
 * midi notes and channels can trigger it.
 * The SynthesizerSound is a passive class that just describes what the sound is -
 * the actual audio rendering for a sound is done by a SynthesizerVoice. This allows
 * more than one SynthesizerVoice to play the same sound at the same time.
 * @see Synthesizer
 * @see SynthesizerVoice
 */
abstract class SynthesizerSound {

    /**
     * Returns true if this sound should be played when a given midi note is pressed.
     * The Synthesizer will use this information when deciding which sounds to trigger
     * for a given note.
     */
    open fun appliesToNote(midiNote: Int): Boolean = false

    /**
     * Returns true if the sound should be triggered by midi events on a given channel.
     * The Synthesizer will use this information when deciding which sounds to trigger
     * for a given note.
     */
    open fun appliesToChannel(channel: Int): Boolean = false

    companion object {
        val EMPTY = object : SynthesizerSound() {}
    }
}