package com.github.basshelal.korgpi.sfz

abstract class SFZHeader {

    abstract val key: String

    val opcodes = mutableListOf<SFZOpcode<*>>()

    enum class HeaderType {
        REGION, GROUP, CONTROL, GLOBAL, CURVE, EFFECT
    }

    companion object {
        fun from(key: String): SFZHeader {
            return object : SFZHeader() {
                override val key: String = key
            }
        }
    }
}

abstract class SFZOpcode<T> {

    abstract val key: String

    abstract val value: T

    companion object {
        fun <T> from(key: String, value: T): SFZOpcode<T> {
            return object : SFZOpcode<T>() {
                override val key: String = key
                override val value: T = value
            }
        }
    }
}