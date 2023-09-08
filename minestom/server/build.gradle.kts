import org.gradle.api.internal.file.FileOperations

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    id("token-replacement")
    id("darkcube-parent")
}

abstract class SetupLibrariesTask : DefaultTask() {
    @get: Inject
    protected abstract val layout: ProjectLayout

    @get: Inject
    protected abstract val fileSystem: FileOperations

    @get: Inject
    protected abstract val objects: ObjectFactory

    @get: Inject
    protected abstract val configurationContainer: ConfigurationContainer

    @get: Inject
    protected abstract val taskContainer: TaskContainer

    @Incremental
    @InputFiles
    val applicationFiles: FileCollection = layout.files(taskContainer.jar)

    @InputFiles
    val runtimeClasspath: FileCollection = layout.files(configurationContainer.runtimeClasspath)

    @OutputDirectory
    val outputDirectory: Provider<Directory> = layout.buildDirectory.dir("setup")

    @TaskAction
    fun setup() {
        fileSystem.delete(outputDirectory)
        fileSystem.mkdir(outputDirectory)
        val librariesDir = outputDirectory.map { out -> out.dir("libraries") }
        fileSystem.mkdir(librariesDir)
        fileSystem.copy {
            from(runtimeClasspath)
            into(librariesDir)
        }
        fileSystem.copy {
            from(applicationFiles.singleFile)
            into(outputDirectory)
            rename(".*", "application.jar")
        }
    }
}

tokens {
    replace("{\$project.version}", project.version)
}

sourceSets {
}

tasks {
    val setupLibraries = register<SetupLibrariesTask>("setupLibraries")
    jar {
        manifest {
            attributes["Main-Class"] = "eu.darkcube.system.minestom.server.Start"
            attributes["Class-Path"] = configurations.runtimeClasspath.get().files.joinToString(separator = " ") { "libraries/" + it.name }
        }
    }
    uploadArtifact {
        dependsOn(jar)
    }
    assemble {
        dependsOn(setupLibraries)
    }
}

dependencies {
    compileOnly("space.vectrix.flare:flare-fastutil:2.0.1")
    compileOnly("org.jctools:jctools-core:4.0.1")
    compileOnlyApi(projects.darkcubesystem.server) {
        exclude("org.jetbrains", "annotations")
    }
    implementation(libs.minestom) {
        exclude("org.jetbrains", "annotations")
    }
}