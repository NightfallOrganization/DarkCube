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
    compileOnlyApi(parent!!.project("common"))
    compileOnlyApi(libs.cloudnetWrapper)
    compileOnlyApi(libs.viaversion)
    compileOnlyApi(libs.viaversion.common)
    compileOnly(libs.luckperms)
    runtimeOnly(project("v1_8_8"))
    runtimeOnly(project("v1_20_1", "reobf"))
    annotationProcessor(libs.cloudnetInjectProcessor)
}
