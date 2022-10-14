import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.7.20"
    application
}

group = "me.martial"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.eventstore:db-client-java:3.0.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.5.1")
    testImplementation("io.kotest:kotest-assertions-core:5.5.1")
    testImplementation("io.kotest:kotest-property:5.5.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
    noArg {
        invokeInitializers = true
    }
}