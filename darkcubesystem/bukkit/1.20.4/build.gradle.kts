/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    alias(libs.plugins.paperweight.userdev)
    `java-library`
}

dependencies {
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.20.4-R0.1-20240412.212303-164")
    implementation(parent!!)
    compileOnly(libs.cloudnetDriver)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
