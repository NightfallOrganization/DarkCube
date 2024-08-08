/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    java
    alias(libs.plugins.shadow)
    id("eu.darkcube.darkcube")
}

tasks {
    jar {
        destinationDirectory = temporaryDir
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier = null

        relocate("com.github.juliarn.npclib", "eu.darkcube.system.woolmania.libs.npclib")

        exclude("**/gson/**")
        exclude("io/github/retrooper/**")
        exclude("com/github/retrooper/**")
        exclude("io/leangen/geantyref/**")
        exclude("io/papermc/lib/**")
        exclude("net/kyori/adventure/**")
        exclude("net/kyori/examination/**")
    }
}

dependencies {
    compileOnly(libs.paper.latest)
    compileOnly(darkcubesystem.bukkit)
    compileOnly("io.github.juliarn", "npc-lib-api", "3.0.0-beta7")
    compileOnly("io.github.juliarn", "npc-lib-common", "3.0.0-beta7")
    implementation("io.github.juliarn", "npc-lib-bukkit", "3.0.0-beta7")
}
