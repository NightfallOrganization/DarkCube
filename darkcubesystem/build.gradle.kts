/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    id("com.github.johnrengelman.shadow")
    id("java-library")
}

tasks {
    shadowJar.configure {
        archiveClassifier = ""
    }
    jar.configure {
        archiveClassifier = "pure"
    }
    assemble.configure { dependsOn(shadowJar) }
}

dependencies {
    api(project("api"))
    runtimeOnly(project("common"))
    runtimeOnly(project("velocity"))
    runtimeOnly(project("bukkit"))
    runtimeOnly(project("module"))
}
