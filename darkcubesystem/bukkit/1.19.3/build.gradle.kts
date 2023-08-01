/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("io.papermc.paperweight.userdev")
    id("java-library")
}

dependencies {
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.19.3-R0.1-SNAPSHOT")
    implementation(parent?.parent!!.project("api"))
    compileOnly(libs.cloudnetDriver)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.release = 17
    }
}
