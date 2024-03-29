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

tasks {
    val finalJar = register<Jar>("finalJar") {
        dependsOn(shadowJar)
        dependsOn(bukkitVersion)
        from(shadowJar.map { jar -> jar.outputs.files.map { zipTree(it) } })
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
    shadowJar {
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
    outgoing.artifact(tasks.named("finalJar"))
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnlyApi(projects.darkcubesystem.bukkit)
    compileOnlyApi(projects.darkcubesystem.implementation.server)
    compileOnlyApi(libs.cloudnet.wrapper)
    compileOnly(libs.viaversion)
    compileOnly(libs.viaversion.common)
    compileOnly(libs.luckperms)

    runtimeOnly(projects.darkcubesystem.implementation.server) { isTransitive = false }
    runtimeOnly(projects.darkcubesystem.bukkit) { isTransitive = false }
    runtimeOnly(projects.darkcubesystem.server) { isTransitive = false }
    bukkitVersion(project("v1_8_8"))
    bukkitVersion(project("latest", "reobf"))
}
