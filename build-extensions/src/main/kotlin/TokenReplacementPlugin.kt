/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.tooling.model.SourceDirectory

class TokenReplacementPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("java")
        val extension = TokenReplacementExtension()
        project.extensions.add("tokens", extension)
        project.afterEvaluate {
            val javaPluginExtension = project.extensions.getByType<JavaPluginExtension>()
            val mainSourceSet = javaPluginExtension.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

            setupReplacementTask(project, "replaceJavaTokens", mainSourceSet.allSource, "tokenized", "compileJava")

        }
    }

    private fun setupReplacementTask(project: Project, name: String, inputSource: SourceDirectorySet, outputPath: String, compileTaskName: String) {
        val outputDir = project.layout.buildDirectory.dir(outputPath)
        val replacementTask = project.tasks.register<TokenReplacement>(name) {
            this.input(inputSource)
            this.output.set(outputDir)
        }

        project.tasks.named(compileTaskName) {
            this.dependsOn(replacementTask)
            if (this is AbstractCompile) {
                this.setSource(outputDir)
            }
        }

    }
}