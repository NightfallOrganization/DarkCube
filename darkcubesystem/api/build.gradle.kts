/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("java-library")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(libs.viaversion)
    compileOnly(libs.viaversion.common)
    compileOnly(libs.cloudnetDriver)
    api(parent!!.project("libs"))
}
