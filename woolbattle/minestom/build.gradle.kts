/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    java
    id("darkcube-parent")
    alias(libs.plugins.shadow)
    id("eu.darkcube.darkcube")
}

val woolbattleShadow by configurations.register("woolbattleShadow")

dependencies {
    implementation(darkcubesystem.minestom)
    implementation(projects.woolbattle.provider)
    implementation(projects.woolbattle.common)
    implementation(libs.luckperms)
    woolbattleShadow(libs.jctools.core)
    woolbattleShadow(libs.fastutil)
    woolbattleShadow(projects.woolbattle.provider) { isTransitive = false }
    woolbattleShadow(projects.woolbattle.api) { isTransitive = false }
    woolbattleShadow(projects.woolbattle.common) { isTransitive = false }
}

tasks {
    val setupArtifactsForUpload = register<Copy>("setupArtifactsForUpload") {
        from(shadowJar.map { it.archiveFile })
        into(temporaryDir)
    }
    register<UploadArtifacts>("uploadArtifacts") {
        dependsOn(setupArtifactsForUpload)
        files.from(setupArtifactsForUpload.map { it.destinationDir })
    }
    jar {
        destinationDirectory = temporaryDir
    }
    shadowJar {
        archiveBaseName = "woolbattle"
        archiveClassifier = null
        configurations = listOf(woolbattleShadow)
        minimize()
        relocate("it.unimi.dsi.fastutil", "eu.darkcube.minigame.woolbattle.libs.fastutil")
        relocate("org.jctools", "eu.darkcube.minigame.woolbattle.libs.jctools")
    }
    assemble {
        dependsOn(shadowJar)
    }
}
