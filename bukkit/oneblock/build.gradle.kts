/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("glyph-width-loader")
    alias(libs.plugins.shadow)
    java
    id("eu.darkcube.darkcube")
}

sourceSets {
    register("generated")
}

tasks {
    register<GlyphWidthLoader>("generateGlyphWidths") {
        version = "latest"
        resourcePacks = listOf("https://aetheria.darkcube.eu/OneBlock.zip")
        outputFile = sourceSets.getByName("generated").output.resourcesDir!!.resolve("glyph-widths.bin")
    }
    processResources.configure {
        dependsOn("generateGlyphWidths")
    }
    jar {
        destinationDirectory = temporaryDir
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        this.archiveClassifier = null
        dependsOn(named(sourceSets.getByName("generated").processResourcesTaskName))
        from(sourceSets.getByName("generated").output.resourcesDir)

        relocate("com.github.juliarn.npclib", "building.oneblock.libs.npclib")

        exclude("**/packetevents/**")
        exclude("**/gson/**")
        exclude("assets/")
        exclude("license.txt")
        exclude("net/kyori/adventure/**")
        exclude("net/kyori/examination/**")

        dependencies {
            include(project(":common:glyph-width-loader"))
            include(dependencyFilter.dependency("io.github.juliarn:npc-lib-bukkit"))
        }
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly(darkcubesystem.bukkit)
    compileOnly(parent!!.project("luckperms-prefixplugin"))
    compileOnly("io.github.juliarn", "npc-lib-api", "3.0.0-beta6")
    compileOnly("io.github.juliarn", "npc-lib-common", "3.0.0-SNAPSHOT")
    implementation("io.github.juliarn", "npc-lib-bukkit", "3.0.0-beta6")
    implementation(project(":common:glyph-width-loader"))
}
