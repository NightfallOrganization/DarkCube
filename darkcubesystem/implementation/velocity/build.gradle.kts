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
    api(projects.darkcubesystem.proxy)
    compileOnly(libs.cloudnet.driver)
    compileOnly(libs.viaversion.velocity)
    compileOnly(libs.viaversion.bukkit)
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
}
