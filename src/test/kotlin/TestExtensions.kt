@file:Suppress("NOTHING_TO_INLINE")

import kotlin.test.assertEquals


internal inline infix fun Any?.mustEqual(expected: Any?) = assertEquals(expected, this)