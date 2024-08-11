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

sourceSets {
    create("generator") {
    }
}

dependencies {
    "generatorImplementation"(libs.gson)
    "generatorImplementation"(libs.fastutil)
    compileOnly(libs.fastutil)
    testRuntimeOnly(libs.fastutil)
}
