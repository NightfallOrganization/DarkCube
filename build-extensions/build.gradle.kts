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

dependencies {
    implementation("com.github.mwiede:jsch:0.2.18")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.13.0")
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