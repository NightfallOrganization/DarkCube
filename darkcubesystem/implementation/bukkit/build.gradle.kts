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
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnlyApi(projects.darkcubesystem.bukkit)
    compileOnlyApi(projects.darkcubesystem.implementation.common)
    compileOnlyApi(libs.cloudnet.wrapper)
    compileOnly(libs.viaversion)
    compileOnly(libs.viaversion.common)
    compileOnly(libs.luckperms)

    runtimeOnly(projects.darkcubesystem.bukkit) { isTransitive = false }
    runtimeOnly(projects.darkcubesystem.server) { isTransitive = false }
//    runtimeOnly(projects.darkcubesystem.implementation.common) { isTransitive = false }
    runtimeOnly(project("v1_8_8"))
    runtimeOnly(project("v1_20_2", "reobf"))
}