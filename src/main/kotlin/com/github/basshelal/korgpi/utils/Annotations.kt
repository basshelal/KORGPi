package com.github.basshelal.korgpi

// Annotation for functions to indicate real time critical code
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION)
annotation class RealTimeCritical