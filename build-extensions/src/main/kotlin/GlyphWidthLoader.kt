/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.process.internal.DefaultJavaExecSpec
import org.gradle.process.internal.ExecActionFactory
import org.gradle.work.InputChanges
import javax.inject.Inject

@CacheableTask
abstract class GlyphWidthLoader : DefaultTask() {

    private val javaLauncher: JavaLauncher
    private val exec: DefaultJavaExecSpec

    init {
        val p = project.project(":common:glyph-width-loader")
        javaLauncher = toolchainService.launcherFor { }.get();
        exec = project.objects.newInstance(DefaultJavaExecSpec::class.java)
        exec.classpath = p.extensions.getByType<SourceSetContainer>().getByName("generator").runtimeClasspath
        inputs.files(p.tasks.getByName("compileGeneratorJava").outputs.files)
    }

    @get: Input
    abstract val version: Property<String>

    @get: Input
    abstract val resourcePacks: ListProperty<String>

    @get: OutputFile
    abstract val outputFile: RegularFileProperty

    @get: Inject
    protected abstract val toolchainService: JavaToolchainService

    @get: Inject
    protected abstract val execActionFactory: ExecActionFactory

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        if (inputChanges.isIncremental) println("Incremental Build")
        else println("Full Build")
        println("Generating glyph widths")
        exec.mainClass.set("eu.darkcube.system.glyphwidthloader.Generator")
        exec.args(version.get(), outputFile.get().asFile.toString())
        exec.args(resourcePacks.get())

        val execAction = execActionFactory.newJavaExecAction()
        exec.copyTo(execAction)
        execAction.executable = javaLauncher.executablePath.toString()
        execAction.execute()
    }
}