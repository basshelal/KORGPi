@file:Suppress("NOTHING_TO_INLINE")

import kotlin.test.assertEquals


inline infix fun Any?.mustEqual(other: Any?) = assertEquals(this, other)