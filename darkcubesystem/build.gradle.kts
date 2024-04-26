/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

val plugins by configurations.register("plugins") {
    isTransitive = false
}
val inject by configurations.register("inject") {
    isTransitive = false
}

tasks {
    register<Jar>("finalJar") {
        dependsOn(shadowJar)
        dependsOn(plugins)
        dependsOn(inject)
        from(shadowJar.get().outputs.files.map { zipTree(it) })
        plugins.run {
            incoming.files.forEach {
                from(it) {
                    this.into("plugins")
                }
            }
        }
        inject.run {
            incoming.files.forEach {
                from(it) {
                    this.into("inject")
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
    runtimeOnly(project("implementation")) {
        exclude(group = libs.cloudnet.driver.get().group)
    }
    plugins(project("implementation:bukkit")) { targetConfiguration = "impl" }
    plugins(project("implementation:velocity"))
    plugins(project("implementation:minestom")) { targetConfiguration = "plugin" }
    inject(project("implementation:minestom")) { targetConfiguration = "inject" }
}
