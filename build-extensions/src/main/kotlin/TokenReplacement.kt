/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.apache.commons.io.file.Counters
import org.gradle.internal.impldep.org.apache.commons.io.file.DeletingPathVisitor
import org.gradle.work.ChangeType
import org.gradle.work.InputChanges
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.name

@CacheableTask
abstract class TokenReplacement : DefaultTask() {

    @get:Inject
    internal abstract val objects: ObjectFactory

    @get: Inject
    internal abstract val fileSystemOperations: FileSystemOperations

    private val loggingReplacements = HashMap<String, String>()

    @Nested
    val sources = LinkedSourceDirectories()

    @Input
    val rules: MutableCollection<TokenReplacementRule> = ArrayList()

    @OutputDirectory
    val output = objects.directoryProperty()

    @TaskAction
    fun run(inputChanges: InputChanges) {
        sources.forEach { node ->
            val name = node.tree.dir.path
            loggingReplacements[output.asFile.get().path] = name
            if (!Files.exists(node.tree.dir.toPath())) return@forEach
            inputChanges.getFileChanges(node.files).forEach {
                val file = it.file.toPath()
                val relative = node.tree.dir.toPath().relativize(file)
                val dest = output.file(relative.toString()).get().asFile.toPath()
                rewrite(file, dest, it.changeType)
            }
        }
    }

    fun input(sourceDirectorySet: SourceDirectorySet) {
        for (srcDirTree in sourceDirectorySet.srcDirTrees) {
            sources.add(project.files(srcDirTree), srcDirTree, srcDirTree.patterns.includes, srcDirTree.patterns.excludes)
        }
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