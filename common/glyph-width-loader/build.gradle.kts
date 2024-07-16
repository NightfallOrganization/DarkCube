/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    `java-library`
    id("eu.darkcube.darkcube")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

sourceSets {
    create("generator") {
    }
}

dependencies {
    "generatorImplementation"("com.google.code.gson:gson:2.11.0")
    "generatorImplementation"("it.unimi.dsi:fastutil:8.5.13")
    compileOnly("it.unimi.dsi:fastutil:8.5.13")
    testRuntimeOnly("it.unimi.dsi:fastutil:8.5.13")
}
