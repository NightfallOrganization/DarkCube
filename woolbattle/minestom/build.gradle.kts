/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    java
    id("darkcube-parent")
    alias(libs.plugins.shadow)
}

val woolbattleShadow by configurations.register("woolbattleShadow")

dependencies {
    implementation(projects.darkcubesystem.minestom)
    implementation(parent!!.project("common"))
    woolbattleShadow(libs.jctools.core)
    woolbattleShadow(libs.fastutil)
    woolbattleShadow(project(":woolbattle:api").setTransitive(false))
    woolbattleShadow(project(":woolbattle:common").setTransitive(false))
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
