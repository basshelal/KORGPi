import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.4.21"
    id("org.openjfx.javafxplugin") version "0.0.8"
}
group = "com.github.basshelal"
version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.ExperimentalUnsignedTypes"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    mainClassName = "com.github.basshelal.korgpi.app.AppKt"
}

javafx {
    version = "14"
    modules = listOf("javafx.controls")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("com.jfoenix:jfoenix:9.0.10")
    implementation("com.diogonunes:JColor:5.0.0")
    implementation("org.jaudiolibs:jnajack:1.4.0")
}