/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("glyph-width-loader")
    alias(libs.plugins.shadow)
    java
}

sourceSets {
    register("generated")
}

tasks {
    register<GlyphWidthLoader>("generateGlyphWidths") {
        dependsOn(named("processGeneratedResources"))
        version = "latest"
        resourcePacks = listOf("https://aetheria.darkcube.eu/Aetheria.zip")
        outputFile = sourceSets.getByName("generated").output.resourcesDir!!.resolve("glyph-widths.bin")
    }
    processResources.configure {
        dependsOn("generateGlyphWidths")
    }
    jar {
        this.archiveClassifier = "pure"
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        this.archiveClassifier = ""
        from(sourceSets.getByName("generated").output.resourcesDir)

        relocate("com.github.juliarn.npclib.relocate.gson", "eu.darkcube.system.libs.com.google.gson")
        relocate("com.github.juliarn.npclib", "eu.darkcube.system.libs.metropolis.com.github.juliarn.npclib")
        relocate("net.kyori.event", "eu.darkcube.system.libs.lobbysystem.net.kyori.event")

        exclude("**/packetevents/**")
        exclude("**/gson/**")
        exclude("assets/")
        exclude("license.txt")
        exclude("net/kyori/adventure/**")
        exclude("net/kyori/examination/**")

        dependencies {
            include(project(":common:glyph-width-loader"))
            include(dependencyFilter.dependency("com.github.juliarn:npc-lib-bukkit"))
        }
    }
}

dependencies {
    runtimeOnly("com.github.juliarn", "npc-lib-bukkit", "3.0.0-SNAPSHOT", null, "shadow", null)
    compileOnly("com.github.juliarn:npc-lib-bukkit:3.0.0-SNAPSHOT")
    compileOnly("com.github.juliarn:npc-lib-common:3.0.0-SNAPSHOT")
    compileOnly("com.github.juliarn:npc-lib-api:3.0.0-SNAPSHOT")

    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly(project(":darkcubesystem"))
    compileOnly(parent!!.project("luckperms-prefixplugin"))
    implementation(project(":common:glyph-width-loader"))
}
