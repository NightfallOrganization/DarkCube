/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

tasks {
    shadowJar {
        archiveClassifier = null
    }
    jar {
        destinationDirectory = temporaryDir
        archiveBaseName = "darkcubesystem-minestom-implementation"
    }
    assemble {
        dependsOn(shadowJar)
    }
}

configurations.register("impl") {
    isCanBeResolved = false
    outgoing.artifact(tasks.shadowJar)
}

dependencies {
    compileOnlyApi(projects.darkcubesystem.implementation.server)
    compileOnlyApi(projects.darkcubesystem.minestom)
    compileOnlyApi(projects.minestom.server)

    // minestom and server api is included in the server bootstrap project because of class loading
}
