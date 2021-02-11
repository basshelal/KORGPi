@file:Suppress("NOTHING_TO_INLINE")

import org.junit.jupiter.api.Assertions.assertEquals


internal inline infix fun Any?.mustEqual(expected: Any?) = assertEquals(expected, this)