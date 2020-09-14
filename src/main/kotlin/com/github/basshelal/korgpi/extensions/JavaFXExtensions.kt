@file:Suppress("NOTHING_TO_INLINE")
@file:JvmMultifileClass
@file:JvmName("Extensions")

package com.github.basshelal.korgpi.extensions

import javafx.stage.Stage

inline var Stage.dimensions: Pair<Number, Number>
    set(value) {
        width = value.first.toDouble()
        height = value.second.toDouble()
    }
    @Deprecated("No Getter", level = DeprecationLevel.ERROR)
    get() = throw NotImplementedError("No Getter")