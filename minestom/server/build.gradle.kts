import org.gradle.api.internal.file.FileOperations

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    id("darkcube-parent")
}

val minestomLibrary by configurations.register("minestomLibrary") {
    isCanBeConsumed = false
    isCanBeResolved = true
}
configurations.api.configure {
    extendsFrom(minestomLibrary)
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
    val runtimeClasspath: FileCollection = layout.files(configurationContainer.named("minestomLibrary"))

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        outputDirectory.convention(layout.dir(objects.property<File?>().convention(temporaryDir)))
    }

    @TaskAction
    fun setup() {
        fileSystem.delete(outputDirectory)
        fileSystem.mkdir(outputDirectory)
        val librariesDir = outputDirectory.dir("libraries")
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

tasks {
    val setupLibraries = register<SetupLibrariesTask>("setupLibraries")
    register<UploadArtifacts>("uploadArtifacts") {
        dependsOn(setupLibraries)
        files.from(setupLibraries.flatMap { it.outputDirectory })
    }
    jar {
        manifest {
            attributes["Main-Class"] = "eu.darkcube.system.minestom.Start"
            attributes["Class-Path"] = minestomLibrary.files.joinToString(separator = " ") { "libraries/" + it.name }
        }
    }
    assemble {
        dependsOn(setupLibraries)
    }
}

dependencies {
    api(projects.darkcubesystem.minestom) {
        exclude("org.jetbrains", "annotations")
    }
    implementation("org.ow2.asm:asm:9.5") // in cloudnet but not exposed
    implementation("org.ow2.asm:asm-tree:9.5") // in cloudnet but not exposed
    // libraries to be added
    minestomLibrary(projects.darkcubesystem.server) { isTransitive = false }
    minestomLibrary(projects.darkcubesystem.implementation.server) { isTransitive = false }
    minestomLibrary(projects.darkcubesystem.minestom) { isTransitive = false }
    minestomLibrary("org.slf4j:jul-to-slf4j:2.0.11")
    minestomLibrary("org.apache.logging.log4j:log4j-core:2.22.1")
    minestomLibrary("org.apache.logging.log4j:log4j-slf4j2-impl:2.22.1")
    minestomLibrary(libs.jline)
    minestomLibrary(libs.bundles.minestom) {
        exclude("org.jetbrains", "annotations")
    }
}