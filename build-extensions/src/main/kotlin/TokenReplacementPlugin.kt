/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.copy.DefaultCopySpec
import org.gradle.api.internal.file.copy.DestinationRootCopySpec
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

class TokenReplacementPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("java")
        val extension = TokenReplacementExtension()
        project.extensions.add("tokens", extension)
        project.afterEvaluate {
            val javaPluginExtension = project.extensions.getByType<JavaPluginExtension>()
            val mainSourceSet = javaPluginExtension.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

            setupReplacementTask(project, "replaceJavaTokens", mainSourceSet.java, "tokenized", "compileJava").configure {
                rules.addAll(extension.rules)
            }
            setupReplacementTask(project, "replaceResourcesTokens", mainSourceSet.resources, "tokenized", "processResources").configure {
                rules.addAll(extension.rules)
            }
        }
    }

    private fun setupReplacementTask(project: Project, name: String, inputSource: SourceDirectorySet, outputPath: String, compileTaskName: String): TaskProvider<TokenReplacement> {
        val outputDir = project.layout.buildDirectory.dir(outputPath).map { it.dir(inputSource.name) }
        val replacementTask = project.tasks.register<TokenReplacement>(name) {
            this.input(inputSource)
            this.output.set(outputDir)
        }

        project.tasks.named(compileTaskName) {
            this.dependsOn(replacementTask)
            if (this is SourceTask) {
                this.setSource(outputDir)
            } else if (this is AbstractCopyTask && this.name == "processResources") {
                // let's go hack the processResources task...
                val source = ((this.rootSpec as DestinationRootCopySpec).children.iterator().next() as DefaultCopySpec).sourceRootsForThisSpec as ConfigurableFileCollection
                // make sure processResources uses our resources
                source.setFrom(outputDir)
            }
        }
        return replacementTask
    }
}