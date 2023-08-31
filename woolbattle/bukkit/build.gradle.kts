/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    alias(libs.plugins.shadow)
    `java-library`
}

tasks {
    shadowJar.configure {
        archiveClassifier = ""
    }
    jar.configure {
        archiveClassifier = "pure"
    }
    assemble.configure {
        dependsOn(shadowJar)
    }
    withType<Jar>().configureEach {
        archiveBaseName = "woolbattle"
    }
}

dependencies {
    compileOnly(project(":darkcubesystem:bukkit"))
    compileOnly(project(":bukkit:statsapi"))
    compileOnly(project(":pserver:pserver-api"))
    compileOnly("io.papermc.paper:paper:1.8.8-R0.1-SNAPSHOT")
    compileOnlyApi(parent!!.project("api"))
    runtimeOnly(parent!!.project("api"))
    compileOnlyApi(libs.cloudnetBridge)
    compileOnlyApi(libs.cloudnetWrapper)
}
