/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import eu.darkcube.build.UploadArtifacts

plugins {
    java
    alias(libs.plugins.shadow)
    id("eu.darkcube.darkcube")
}

val include = configurations.register("include")
configurations.named("implementation").configure { extendsFrom(include.get()) }

dependencies {
    implementation(libs.paper.latest)
    implementation(darkcubesystem.bukkit)
    include(libs.hikaricp)
    include(libs.mariadb.java.client)
}

tasks {
    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        destinationDirectory = temporaryDir
    }
    shadowJar {
        configurations = listOf(include.get())
        archiveClassifier = null
        relocate("com.github.benmanes.caffeine", "eu.darkcube.system.bauserver.libs.caffeine")
        relocate("com.google.errorprone", "eu.darkcube.system.bauserver.libs.errorprone")
        relocate("com.sun.jna", "eu.darkcube.system.bauserver.libs.jna")
        relocate("com.zaxxer.hikari", "eu.darkcube.system.bauserver.libs.hikari")
        relocate("org.apache.commons.logging", "eu.darkcube.system.bauserver.libs.apache.commons.logging")
        relocate("org.checkerframework", "eu.darkcube.system.bauserver.libs.checkerframework")
        relocate("org.mariadb", "eu.darkcube.system.bauserver.libs.mariadb")
        relocate("waffle", "eu.darkcube.system.bauserver.libs.waffle")
        exclude("org/slf4j/**")
    }
    register<UploadArtifacts>("uploadArtifacts") {
        dependsOn(shadowJar)
        files.from(shadowJar.flatMap { it.archiveFile })
    }
    assemble {
        dependsOn(shadowJar)
    }
}