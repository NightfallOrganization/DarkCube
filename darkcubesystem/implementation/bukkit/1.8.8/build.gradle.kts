/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    `java-library`
}

dependencies {
    compileOnly("io.papermc.paper:paper:1.8.8-R0.1-SNAPSHOT")
    compileOnly(parent!!)
    compileOnly(libs.viaversion)
    compileOnly(libs.viaversion.common)
    compileOnly(libs.cloudnet.driver)
}
