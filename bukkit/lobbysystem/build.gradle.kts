/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar.configure {
        minimize()
        archiveClassifier = ""
        relocate("com.github.unldenis.hologram", "eu.darkcube.system.libs.lobbysystem.com.github.unldenis.hologram")
        relocate("com.github.juliarn.npclib.relocate.gson", "eu.darkcube.system.libs.com.google.gson")
        relocate("com.github.juliarn.npclib", "eu.darkcube.system.libs.lobbysystem.com.github.juliarn.npclib")
        relocate("net.kyori", "eu.darkcube.system.libs.lobbysystem.net.kyori")
        exclude("assets/")
        exclude("**/packetevents/**")
        exclude("**/gson/**")
        exclude("me/clip/placeholderapi/**")
        exclude("license.txt")
        exclude("input")
        exclude("classpath.index")
        dependencies {
            include(dependencyFilter.dependency("com.github.juliarn.npc-lib:npc-lib-bukkit"))
            include(dependencyFilter.dependency("com.github.juliarn.npc-lib:npc-lib-labymod"))
            include(dependencyFilter.dependency("com.github.unldenis.hologram:hologram-lib"))
            include(dependencyFilter.dependency("me.clip:placeholderapi"))
            include(project(":common:labymod-emotes"))
        }
    }
    jar.configure { archiveClassifier = "pure" }
    assemble.configure { dependsOn(shadowJar) }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(project(":darkcubesystem"))
    compileOnly(project(":darkcubesystem:bukkit"))
    compileOnly(project(":pserver:pserver-bukkit"))
    compileOnly(project(":pserver:pserver-api"))
    api(project(":common:labymod-emotes"))
    implementation("com.github.juliarn.npc-lib:npc-lib-bukkit:3.0.0-beta5")
    implementation("com.github.juliarn.npc-lib:npc-lib-labymod:3.0.0-beta5")
    implementation(libs.hologramlib)
    implementation("me.clip:placeholderapi:2.11.3")
    compileOnly(libs.annotations)
    compileOnly(libs.cloudnetBridge)
    compileOnly(libs.luckperms)
}
