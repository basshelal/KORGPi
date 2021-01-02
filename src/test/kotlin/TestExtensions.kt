@file:Suppress("NOTHING_TO_INLINE")

import kotlin.test.assertEquals


internal inline infix fun Any?.mustEqual(other: Any?) = assertEquals(this, other)