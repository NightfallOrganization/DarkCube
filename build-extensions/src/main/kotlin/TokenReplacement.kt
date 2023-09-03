/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFile
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.internal.impldep.org.apache.commons.io.file.Counters
import org.gradle.internal.impldep.org.apache.commons.io.file.DeletingPathVisitor
import org.gradle.work.ChangeType
import org.gradle.work.InputChanges
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.function.Supplier
import java.util.regex.Pattern
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

    @Input
    val rules: MutableCollection<TokenReplacementRule> = ArrayList()

    @OutputDirectory
    val output = objects.directoryProperty()

    @TaskAction
    fun run(inputChanges: InputChanges) {
        val outputFile = output.asFile.get()
        if (!outputFile.exists()) outputFile.mkdirs()
        sources.forEach { node ->
            if (!Files.exists(node.tree.dir.toPath())) return@forEach
            inputChanges.getFileChanges(node.files).forEach {
                val dest = destination(it.file.toPath(), node.tree.dir.toPath()).get().asFile.toPath()
                val file = it.file.toPath()
                rewrite(file, dest, it.changeType)
            }
        }
    }

    fun input(sourceDirectorySet: SourceDirectorySet) {
        for (srcDirTree in sourceDirectorySet.srcDirTrees) {
            sources.add(project.files(srcDirTree), srcDirTree, srcDirTree.patterns.includes, srcDirTree.patterns.excludes)
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
        var content: String = Files.readString(file)
        logger.info("Rewrite " + file.name)
        for (rule: TokenReplacementRule in rules) {
            content = content.replace(rule.getCompiledRegex(), rule.value)
        }
        Files.writeString(dest, content)
    }

    private fun rewrite(file: Path, dest: Path, change: ChangeType) {
        when (change) {
            ChangeType.REMOVED -> {
                if (Files.isDirectory(dest))
                    Files.walkFileTree(dest, DeletingPathVisitor(Counters.noopPathCounters()))
                else
                    Files.deleteIfExists(dest)
            }

            ChangeType.ADDED, ChangeType.MODIFIED -> {
                if (Files.isRegularFile(file)) {
                    Files.createDirectories(dest.parent)
                    work(file, dest)
                } else if (Files.isDirectory(file)) {
                    Files.createDirectories(dest)
                }
            }
        }
    }
}