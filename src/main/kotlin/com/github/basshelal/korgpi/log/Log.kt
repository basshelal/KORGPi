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

inline fun logE(message: Any? = "") = Log.e(message)