@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import org.jaudiolibs.jnajack.Jack
import org.jaudiolibs.jnajack.JackClient
import org.jaudiolibs.jnajack.JackMidi
import org.jaudiolibs.jnajack.JackOptions
import org.jaudiolibs.jnajack.JackPort

inline fun Jack.openClient(name: String): JackClient {
    return this.openClient(name, EnumSet(JackOptions.JackNoStartServer), null)
}

inline val JackPort.midiEventCount: Int get() = JackMidi.getEventCount(this)

inline fun JackPort.getEvent(index: Int, event: JackMidi.Event): JackMidi.Event {
    JackMidi.eventGet(event, this, index)
    return event
}