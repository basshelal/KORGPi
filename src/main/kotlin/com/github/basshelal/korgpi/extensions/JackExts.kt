@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import org.jaudiolibs.jnajack.Jack
import org.jaudiolibs.jnajack.JackClient
import org.jaudiolibs.jnajack.JackOptions

inline fun Jack.openClient(name: String): JackClient {
    return this.openClient(name, EnumSet(JackOptions.JackNoStartServer), null)
}