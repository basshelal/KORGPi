@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import javax.sound.midi.ShortMessage

inline val ShortMessage.details: String
    get() {
        return "Command: $command, channel: $channel, Data1: $data1, Data2: $data2"
    }