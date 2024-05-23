/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    alias(libs.plugins.shadow)
    id("eu.darkcube.darkcube")
}

tasks {
    jar {
        destinationDirectory = temporaryDir
    }
    shadowJar {
        archiveClassifier = null
    }
    assemble {
        dependsOn(shadowJar)
    }
}

dependencies {
    compileOnlyApi(libs.cloudnet.node)
    compileOnlyApi(libs.cloudnet.bridge)
//    compileOnlyApi project(':darkcubesystem:module')
    api(project(":pserver:pserver-api"))
    runtimeOnly(project(":pserver:pserver-bukkit"))
}
