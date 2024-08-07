/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    alias(libs.plugins.shadow)
    java
    id("eu.darkcube.darkcube")
}

tasks {
    shadowJar.configure {
        archiveClassifier = ""
    }
    jar.configure {
        destinationDirectory = temporaryDir
    }
    assemble.configure {
        dependsOn(shadowJar)
    }
    withType<Jar>().configureEach {
        archiveBaseName = "woolbattle"
    }
}

dependencies {
    compileOnly(darkcubesystem.bukkit)
    compileOnly(project(":bukkit:statsapi"))
    compileOnly(project(":pserver:pserver-api"))
    compileOnly("io.papermc.paper:paper:1.8.8-R0.1-SNAPSHOT")
    compileOnly(projects.woolbattle.common)
    runtimeOnly(projects.woolbattle.api) {
        isTransitive = false
    }
    compileOnly(libs.cloudnet.bridge)
    compileOnly(libs.cloudnet.wrapper)
}
