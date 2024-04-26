/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.gradle.jvm.tasks.Jar

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

val bukkitVersion: Configuration by configurations.creating {
    isCanBeConsumed = false
}
val shadowContent by configurations.register("shadowContent") { isTransitive = false }

val finalJar = tasks.register<Jar>("finalJar") {
    dependsOn(tasks.shadowJar)
    dependsOn(bukkitVersion)
    from(tasks.shadowJar.map { jar -> jar.outputs.files.map { zipTree(it) } })
    bukkitVersion.run {
        incoming.files.forEach {
            val zip = zipTree(it)
            val version = zip.matching { include("version-info") }.singleFile.readText()
            from(it) {
                rename { "$version.jar" }
                into("versions")
            }
        }
    }
}

tasks {
    shadowJar {
        configurations = listOf(shadowContent)
        destinationDirectory = temporaryDir
    }
    jar {
        destinationDirectory = temporaryDir
    }
    assemble {
        dependsOn(finalJar)
    }
}

configurations.register("impl") {
    isCanBeResolved = false
    outgoing.artifact(finalJar)
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    api(projects.darkcubesystem.bukkit)
    api(projects.darkcubesystem.implementation.server)
    compileOnlyApi(libs.cloudnet.wrapper)
    implementation(libs.viaversion)
    implementation(libs.viaversion.common)
    implementation(libs.luckperms)

    shadowContent(projects.darkcubesystem.implementation.server)
    shadowContent(projects.darkcubesystem.bukkit)
    shadowContent(projects.darkcubesystem.server)
    bukkitVersion(project("v1_8_8"))
    bukkitVersion(project("latest", "reobf"))
}
