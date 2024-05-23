/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    `java-library`
    id("eu.darkcube.darkcube")
    alias(libs.plugins.shadow)
}

tasks {
    shadowJar.configure {
        minimize()
        archiveClassifier = ""
        relocate("com.github.unldenis.hologram", "eu.darkcube.system.libs.lobbysystem.com.github.unldenis.hologram")
        relocate("com.github.juliarn.npclib.relocate.gson", "eu.darkcube.system.libs.com.google.gson")
        relocate("com.github.juliarn.npclib", "eu.darkcube.system.libs.lobbysystem.com.github.juliarn.npclib")
        relocate("net.kyori.event", "eu.darkcube.system.libs.lobbysystem.net.kyori.event")
        exclude("assets/")
        exclude("**/gson/**")
        exclude("**/packetevents/**")
        exclude("net/kyori/adventure/**")
        exclude("net/kyori/examination/**")
        exclude("me/clip/placeholderapi/**")
        exclude("license.txt")
        exclude("input")
        exclude("classpath.index")
        dependencies {
            include(dependencyFilter.dependency("io.github.juliarn:npc-lib-bukkit"))
            include(dependencyFilter.dependency("io.github.juliarn:npc-lib-labymod"))
            include(dependencyFilter.dependency("com.github.unldenis.hologram:hologram-lib"))
            include(dependencyFilter.dependency("me.clip:placeholderapi"))
            include(project(":common:labymod-emotes"))
        }
    }
    jar.configure {
        destinationDirectory = layout.buildDirectory.dir("tmp").map { d -> d.dir("jar") }
    }
    assemble.configure { dependsOn(shadowJar) }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(darkcubesystem.bukkit)
    compileOnly(project(":pserver:pserver-bukkit"))
    compileOnly(project(":pserver:pserver-api"))
    api(project(":common:labymod-emotes"))
//    runtimeOnly("com.github.juliarn", "npc-lib-bukkit", "3.0.0-SNAPSHOT", classifier = "shadow")
    implementation("io.github.juliarn:npc-lib-bukkit:3.0.0-beta6")
    compileOnly("io.github.juliarn:npc-lib-common:3.0.0-beta6")
    compileOnly("io.github.juliarn:npc-lib-api:3.0.0-beta6")
    implementation("io.github.juliarn:npc-lib-labymod:3.0.0-beta6")
    implementation(libs.hologramlib)
    implementation("me.clip:placeholderapi:2.11.3")
    compileOnly(libs.annotations)
    compileOnly(libs.cloudnet.bridge)
    compileOnly(libs.luckperms)
    compileOnly(libs.cloudnet.inject.api)
    annotationProcessor(libs.cloudnet.inject.processor)
}
