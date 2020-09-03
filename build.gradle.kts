import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.4.0"
    id("org.openjfx.javafxplugin") version "0.0.8"
}
group = "com.github.basshelal"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "com.github.basshelal.korgpi.MainKt"
}

javafx {
    version = "14"
    modules = listOf("javafx.controls")
}

repositories {
    mavenCentral()
}
dependencies {
    testImplementation(kotlin("test-junit5"))
    implementation( "no.tornado:tornadofx:1.7.20")
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}