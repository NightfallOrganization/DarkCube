/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class DarkCubeParent : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register<UploadArtifact>("uploadArtifact")
    }
}