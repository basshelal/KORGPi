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
//  for audio playback, JUCE uses Reentrant Locks when necessary and indeed we need synchronized
//  blocks all over this bitch to ensure no CMEs or any weird concurrency issues
class Synthesizer(private val voices: MutableList<SynthesizerVoice> = mutableListOf()) {

}

/**
 * Represents a voice that a Synthesizer can use to play a SynthesizerSound.
 * A voice plays a single sound at a time, and a synthesiser holds an array of
 * voices so that it can play polyphonically.
 * @see Synthesizer
 * @see SynthesizerSound
 */
class SynthesizerVoice(val sound: SynthesizerSound = SynthesizerSound.EMPTY) {

    /**
     * Returns the midi note that this voice is currently playing.
     * Returns a value less than 0 if no note is playing.
     */
    fun getCurrentPlayingNote() {}

    /**
     * Returns the sound that this voice is currently playing.
     * Returns nullptr if it's not playing.
     */
    fun getCurrentPlayingSound() {}

    /**
     * Must return true if this voice object is capable of playing the given sound.
     * If there are different classes of sound, and different classes of voice, a voice can choose which ones it
     * wants to take on.
     * A typical implementation of this method may just return true if there's only one type
     * of voice and sound, or it might check the type of the sound object passed-in and
     * see if it's one that it understands.
     */
    fun canPlaySound(sound: SynthesizerSound): Boolean = true

    /**
     * Called to start a new note.
     * This will be called during the rendering callback, so must be fast and thread-safe.
     */
    fun startNote(/*int midiNoteNumber, float velocity, SynthesiserSound* sound, int currentPitchWheelPosition*/) {}


    /**
     * Called to stop a note.
     * This will be called during the rendering callback, so must be fast and thread-safe.
     * The velocity indicates how quickly the note was released - 0 is slowly, 1 is quickly.
     * If allowTailOff is false or the voice doesn't want to tail-off, then it must stop all
     * sound immediately, and must call clearCurrentNote() to reset the state of this voice
     * and allow the synth to reassign it another sound.
     * If allowTailOff is true and the voice decides to do a tail-off, then it's allowed to
     * begin fading out its sound, and it can stop playing until it's finished. As soon as it
     * finishes playing (during the rendering callback), it must make sure that it calls
     * clearCurrentNote().
     */
    fun stopNote(/*float velocity, bool allowTailOff*/) {}

    /**
     * Returns true if this voice is currently busy playing a sound.
     * By default this just checks the getCurrentlyPlayingNote() value, but can
     * be overridden for more advanced checking.
     */
    fun isVoiceActive(): Boolean = false


    /**
     * Called to let the voice know that the pitch wheel has been moved.
     * This will be called during the rendering callback, so must be fast and thread-safe.
     */
    fun pitchWheelMoved(/*int newPitchWheelValue*/) {}

    /** Called to let the voice know that a midi controller has been moved.
    This will be called during the rendering callback, so must be fast and thread-safe.
     */
    fun controllerMoved(/*int controllerNumber, int newControllerValue*/) {}

    /**
     * Called to let the voice know that the aftertouch has changed.
     * This will be called during the rendering callback, so must be fast and thread-safe.
     */
    fun aftertouchChanged(/*int newAftertouchValue*/) {}

    /**
     * Called to let the voice know that the channel pressure has changed.
     * This will be called during the rendering callback, so must be fast and thread-safe.
     */
    fun channelPressureChanged(/*int newChannelPressureValue*/) {}

    //==============================================================================
    /** Renders the next block of data for this voice.

    The output audio data must be added to the current contents of the buffer provided.
    Only the region of the buffer between startSample and (startSample + numSamples)
    should be altered by this method.

    If the voice is currently silent, it should just return without doing anything.

    If the sound that the voice is playing finishes during the course of this rendered
    block, it must call clearCurrentNote(), to tell the synthesiser that it has finished.

    The size of the blocks that are rendered can change each time it is called, and may
    involve rendering as little as 1 sample at a time. In between rendering callbacks,
    the voice's methods will be called to tell it about note and controller events.
     */
    fun renderNextBlock(/*AudioBuffer<float>& outputBuffer, int startSample, int numSamples*/) {}

    /** A double-precision version of renderNextBlock() */
    // fun renderNextBlock (/*AudioBuffer<double>& outputBuffer,int startSample,int numSamples*/) {}

    /** Changes the voice's reference sample rate.

    The rate is set so that subclasses know the output rate and can set their pitch
    accordingly.

    This method is called by the synth, and subclasses can access the current rate with
    the currentSampleRate member.
     */
    fun setCurrentPlaybackSampleRate(/*double newRate*/) {}

    /** Returns true if the voice is currently playing a sound which is mapped to the given
    midi channel.

    If it's not currently playing, this will return false.
     */
    fun isPlayingChannel(/*int midiChannel*/) {}

    /** Returns the current target sample rate at which rendering is being done.
    Subclasses may need to know this so that they can pitch things correctly.
     */
    fun getSampleRate(): Double = 0.0

    /** Returns true if the key that triggered this voice is still held down.
    Note that the voice may still be playing after the key was released (e.g because the
    sostenuto pedal is down).
     */
    fun isKeyDown(): Boolean = false

    /** Allows you to modify the flag indicating that the key that triggered this voice is still held down.
    @see isKeyDown
     */
    fun setKeyDown(/*bool isNowDown*/) {}

    /** Returns true if the sustain pedal is currently active for this voice. */
    fun isSustainPedalDown(): Boolean = false

    /** Modifies the sustain pedal flag. */
    fun setSustainPedalDown(/*bool isNowDown*/) {}

    /** Returns true if the sostenuto pedal is currently active for this voice. */
    fun isSostenutoPedalDown(): Boolean = false

    /** Modifies the sostenuto pedal flag. */
    fun setSostenutoPedalDown(/*bool isNowDown*/) {}

    /** Returns true if a voice is sounding in its release phase **/
    fun isPlayingButReleased(): Boolean = false
    /*{
        return isVoiceActive() && !(isKeyDown() || isSostenutoPedalDown() || isSustainPedalDown());
    }*/

    /** Returns true if this voice started playing its current note before the other voice did. */
    fun wasStartedBefore(/*const SynthesiserVoice& other*/): Boolean = false

    /** Resets the state of this voice after a sound has finished playing.

    The subclass must call this when it finishes playing a note and becomes available
    to play new ones.

    It must either call it in the stopNote() method, or if the voice is tailing off,
    then it should call it later during the renderNextBlock method, as soon as it
    finishes its tail-off.

    It can also be called at any time during the render callback if the sound happens
    to have finished, e.g. if it's playing a sample and the sample finishes.
     */
    /*protected*/  fun clearCurrentNote() {}

    // private :
    //==============================================================================
    // friend class Synthesiser;
    // double currentSampleRate = 44100.0;
    // int currentlyPlayingNote = -1, currentPlayingMidiChannel = 0;
    // uint32 noteOnTime = 0;
    // SynthesiserSound::Ptr currentlyPlayingSound;
    // bool keyIsDown = false, sustainPedalDown = false, sostenutoPedalDown = false;
    // AudioBuffer<float> tempBuffer;
    // JUCE_LEAK_DETECTOR (SynthesiserVoice)

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
    abstract fun appliesToNote(midiNote: Int): Boolean

    /**
     * Returns true if the sound should be triggered by midi events on a given channel.
     * The Synthesizer will use this information when deciding which sounds to trigger
     * for a given note.
     */
    abstract fun appliesToChannel(channel: Int): Boolean

    companion object {
        val EMPTY = object : SynthesizerSound() {
            override fun appliesToNote(midiNote: Int) = false
            override fun appliesToChannel(channel: Int) = false
        }
    }
}