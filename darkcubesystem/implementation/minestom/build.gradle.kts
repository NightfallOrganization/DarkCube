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
        archiveClassifier = "pure"
    }
    assemble {
        dependsOn(shadowJar)
    }
}

dependencies {
    compileOnlyApi(projects.darkcubesystem.implementation.common)
    compileOnlyApi(projects.darkcubesystem.server)
    compileOnlyApi(libs.minestom)

    runtimeOnly(projects.darkcubesystem.implementation.common) { isTransitive = false }
    runtimeOnly(projects.darkcubesystem.server) { isTransitive = false }
}
