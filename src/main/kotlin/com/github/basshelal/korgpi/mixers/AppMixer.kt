@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.mixers

// TODO: 06/10/2020 GitHub Issue #1
// The Global Mixer instance for the entire app that combines all Mixer functionality
object AppMixer

/*
 * Connections:
 * Readable Line (Audio In) to Writable Line (Audio Out): A thread that reads and then writes back what was read
 * Midi In to Writable Line: Synthesizer that transforms events into audio
 * Midi In to Midi Out: Midi Passthrough or transform, either send out the Midi messages or transform them
 *
 * Audio in to Midi Out is not possible because its an inverse transform
 */