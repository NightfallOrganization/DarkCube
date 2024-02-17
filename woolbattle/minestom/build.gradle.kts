/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    id("darkcube-parent")
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnlyApi(projects.darkcubesystem.minestom)
    api(parent!!.project("common"))
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
    }
    assemble {
        dependsOn(shadowJar)
    }
}
