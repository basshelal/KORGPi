@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import javax.sound.midi.MidiDevice
import javax.sound.midi.ShortMessage

inline val MidiDevice.details: String
    get() = deviceInfo.let {
        """Type: ${this.javaClass.simpleName}
        |name: ${it.name}
        |version: ${it.version}
        |vendor: ${it.vendor}
        |description: ${it.description}
        |isOpen: ${this.isOpen}
        |receivers: ${this.receivers.size}
        |transmitters: ${this.transmitters.size}
        |maxReceivers: ${this.maxReceivers}
        |maxTransmitters: ${this.maxTransmitters}
        """.trimMargin()
    }

inline val ShortMessage.details: String
    get() {
        return "Command: $command, channel: $channel, Data1: $data1, Data2: $data2"
    }