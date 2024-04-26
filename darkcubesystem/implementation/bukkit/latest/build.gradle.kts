/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    alias(libs.plugins.paperweight.userdev)
    `java-library`
}

dependencies {
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.20.2-R0.1-20231113.183409-94")
    implementation(parent!!)
    compileOnly(libs.cloudnet.driver)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    jar {
        destinationDirectory = temporaryDir
    }
}

configurations {
//    default {
//        artifacts.clear()
//        setExtendsFrom(emptyList())
//        outgoing.artifact(tasks.reobfJar)
//    }
}

