/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
}

dependencies {
    api(darkcubesystem.implementation.common)
    api(darkcubesystem.server)
}

tasks {
    jar {
        archiveBaseName = "darkcubesystem-server-implementation"
    }
}
