@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.korgpi.log

object Log {

    var enabled: Boolean = true
    var beforeLog: () -> Unit = {}
    var afterLog: () -> Unit = {}

    fun d(message: Any? = "") {
        if (enabled) {
            beforeLog()
            println(message)
            afterLog()
        }
    }

}