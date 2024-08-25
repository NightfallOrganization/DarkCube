/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    java
    alias(libs.plugins.shadow)
    id("darkcube-parent")
    id("eu.darkcube.darkcube")
}

dependencies {
    compileOnly(libs.paper.latest)
    compileOnly(darkcubesystem.bukkit)
    compileOnly(darkcubesystem.kyori.wrapper)
    compileOnly("io.github.juliarn", "npc-lib-api", "3.0.0-beta7")
    compileOnly("io.github.juliarn", "npc-lib-common", "3.0.0-beta7")
    implementation("io.github.juliarn", "npc-lib-bukkit", "3.0.0-beta7")
}

tasks {
    jar {
        destinationDirectory = temporaryDir
    }
    register<UploadArtifacts>("uploadArtifact") {
        dependsOn(shadowJar)
        files.from(shadowJar.map { it.outputs.files.singleFile })
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier = null

        relocate("com.github.juliarn.npclib.relocate.com.packetevents", "com.github.retrooper")
        relocate("com.github.juliarn.npclib.relocate.io.packetevents", "io.github.retrooper")
        relocate("com.github.juliarn.npclib", "eu.darkcube.system.woolmania.libs.npclib")

        exclude("assets/")
        exclude("**/gson/**")
        exclude("**/packetevents/**")
        exclude("**/packetevents/**")
        exclude("io/github/retrooper/**")
        exclude("com/github/retrooper/**")
        exclude("io/leangen/geantyref/**")
        exclude("io/papermc/lib/**")
        exclude("net/kyori/adventure/**")
        exclude("net/kyori/examination/**")
    }
}