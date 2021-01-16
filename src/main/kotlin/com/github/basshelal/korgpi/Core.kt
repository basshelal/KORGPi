package com.github.basshelal.korgpi

data class MinMax<T>(val min: T, val max: T) {

    override fun toString(): String = "min: $min, max: $max"
}