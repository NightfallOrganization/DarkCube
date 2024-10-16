/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    java
    id("eu.darkcube.darkcube")
}

dependencies {
    compileOnly(libs.paper.latest)
    compileOnly(darkcubesystem.bukkit)
}

tasks {
    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }
}