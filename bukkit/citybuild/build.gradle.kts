/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("glyph-width-loader")
    java
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

sourceSets {
    register("generated")
}

tasks {
    register<GlyphWidthLoader>("generateGlyphWidths") {
        version = "latest"
        resourcePacks = listOf("https://metropolis.darkcube.eu/Metropolis.zip")
        outputFile = sourceSets.getByName("generated").output.resourcesDir!!.resolve("glyph-widths.bin")
    }
    processResources.configure {
        dependsOn("generateGlyphWidths")
    }
    jar {
        from(sourceSets.getByName("generated").output.resourcesDir)
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnlyApi(project(":darkcubesystem"))
    compileOnlyApi(parent!!.project("luckperms-prefixplugin"))
}
