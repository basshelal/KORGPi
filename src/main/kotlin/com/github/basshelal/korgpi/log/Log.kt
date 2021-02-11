@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.log

object Log {

    var enabled: Boolean = true
    var beforeLog: () -> Unit = {}
    var afterLog: () -> Unit = {}

    fun d(message: Any? = "") {
        if (enabled) {
            beforeLog()
            System.out.println(message)
            afterLog()
        }
    }

    fun e(message: Any? = "") {
        if (enabled) {
            beforeLog()
            System.err.println(message)
            afterLog()
        }
    }

}

inline fun logD(message: Any? = "") = Log.d(message)

inline fun logD(vararg messages: Any? = emptyArray()) = Log.d(messages.joinToString(" "))

inline fun logDAll(vararg messages: Any?) = messages.forEach { Log.d(it) }

inline fun logE(message: Any? = "") = Log.e(message)

inline fun logEAll(vararg messages: Any?) = messages.forEach { Log.e(it) }

inline fun Any?.log(messages: Any? = "") = logD(this, messages)