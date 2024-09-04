/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import eu.darkcube.build.UploadArtifacts

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

repositories {
    // Used for AtlasProjectiles
    maven("https://reposilite.worldseed.online/public")
}

dependencies {
    implementation(darkcubesystem.minestom) {
        exclude("eu.darkcube", "minestom")
    }
    implementation(projects.minestom.server)
    implementation(projects.woolbattle.provider)
    implementation(projects.woolbattle.common)
    implementation(libs.luckperms)
    implementation("ca.atlasengine:atlas-projectiles:1.0.2")
    woolbattleShadow(libs.jctools.core)
    woolbattleShadow("ca.atlasengine:atlas-projectiles:1.0.2")
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
        relocate("org.jctools", "eu.darkcube.minigame.woolbattle.libs.jctools")
    }
    assemble {
        dependsOn(shadowJar)
    }
}
