/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
}

repositories {
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register("glyphWidthLoader") {
            id = "glyph-width-loader"
            implementationClass = "GlyphWidthLoaderPlugin"
        }
        register("tokenReplacement") {
            id = "token-replacement"
            implementationClass = "TokenReplacementPlugin"
        }
        register("darkcubeParent") {
            id = "darkcube-parent"
            implementationClass = "DarkCubeParent"
        }
    }
}