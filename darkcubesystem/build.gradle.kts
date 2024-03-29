/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

configurations {
    register("embed") {
        isTransitive = false
    }
}

tasks {
    register<Jar>("finalJar") {
        dependsOn(shadowJar)
        dependsOn(configurations.named("embed"))
        from(shadowJar.get().outputs.files.map { zipTree(it) })
        configurations.named("embed").configure {
            incoming.files.forEach {
                from(it) {
                    this.into("plugins")
                }
            }
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveClassifier = null
    }
    shadowJar.configure {
        destinationDirectory = temporaryDir
    }
    jar.configure {
        destinationDirectory = temporaryDir
    }
    assemble.configure {
        dependsOn(getByName("finalJar"))
    }
}

dependencies {
    api(project("api"))
    runtimeOnly(project("implementation"))
    "embed"(project("implementation:bukkit", "impl"))
    "embed"(project("implementation:velocity"))
    "embed"(project("implementation:minestom", "impl"))
}
