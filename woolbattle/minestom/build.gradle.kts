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
    jar {
        archiveClassifier = "pure"
    }
    shadowJar {
        archiveBaseName = "application"
        archiveClassifier = null
        manifest {
            attributes["Main-Class"] = "eu.darkcube.system.woolbattle.Start"
        }
    }
    assemble {
        dependsOn(shadowJar)
    }
}

dependencies {
    implementation("net.minestom:minestom")
}