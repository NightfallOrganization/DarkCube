/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

val shadowContent by configurations.register("shadowContent")
val extensionSourceSet by sourceSets.register("extension")

val extensionJar = tasks.register<Jar>("extensionJar") {
    from(extensionSourceSet.output)
    destinationDirectory = temporaryDir
}

tasks {
    shadowJar {
        configurations = listOf(shadowContent)
        archiveClassifier = null
        destinationDirectory = temporaryDir
    }
}

configurations.named("extensionCompileOnly").configure {
    extendsFrom(configurations.compileClasspath.get())
}

configurations.register("plugin") {
    isCanBeResolved = false
    outgoing.artifact(extensionJar) {
        this.name = "minestom"
        this.classifier = ""
    }
}
configurations.register("inject") {
    isCanBeResolved = false
    outgoing.artifact(tasks.shadowJar) {
        this.name = "minestom"
        this.classifier = ""
    }
}

dependencies {
    api(darkcubesystem.implementation.server)
    api(darkcubesystem.minestom)
    "extensionImplementation"(sourceSets.main.map { it.output })

    shadowContent(darkcubesystem.implementation.server) { isTransitive = false }
    shadowContent(darkcubesystem.minestom) { isTransitive = false }
    shadowContent(darkcubesystem.server) { isTransitive = false }
}
