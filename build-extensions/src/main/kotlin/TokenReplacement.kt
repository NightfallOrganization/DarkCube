/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryTree
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFile
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import javax.inject.Inject
import kotlin.io.path.name
import kotlin.io.path.relativeTo

@CacheableTask
abstract class TokenReplacement : DefaultTask() {

    @get:Inject
    internal abstract val objects: ObjectFactory

    @get: Inject
    internal abstract val fileSystemOperations: FileSystemOperations

    @Nested
    val sources = LinkedSourceDirectories()

    @OutputDirectory
    val output = objects.directoryProperty()

    @TaskAction
    fun run(inputChanges: InputChanges) {
        val outputFile = output.asFile.get()
        if (!outputFile.exists()) outputFile.mkdirs()
        sources.forEach { node ->
            println(node.tree.dir)
            if (!Files.exists(node.tree.dir.toPath())) return@forEach
            inputChanges.getFileChanges(node.files).forEach {
                rewrite(it.file.toPath(), destination(it.file.toPath(), node.tree.dir.toPath()).get().asFile.toPath(), it.changeType)
            }
        }
    }

    fun input(sourceDirectorySet: SourceDirectorySet) {
        for (srcDirTree in sourceDirectorySet.srcDirTrees) {
            sources.add(project.files(srcDirTree), srcDirTree)
        }
    }

    private fun destination(file: Path, base: Path): Provider<RegularFile> {
        return output.file(relative(file, base).toString())
    }

    private fun relative(file: Path, base: Path): Path {
        return file.relativeTo(base)
    }

    private fun work(file: Path, dest: Path) {
        if (Files.isDirectory(file)) return
        val content: String = Files.readString(file)
        println("Rewrite " + file.name)
    }

    private fun rewrite(file: Path, dest: Path, change: ChangeType) {
        when (change) {
            ChangeType.REMOVED -> {
                Files.delete(dest)
            }

            ChangeType.ADDED, ChangeType.MODIFIED -> {
                Files.createDirectories(dest.parent)
                work(file, dest)
            }
        }
    }
}