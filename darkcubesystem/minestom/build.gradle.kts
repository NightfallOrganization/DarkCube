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
    compileOnly("org.ow2.asm:asm:9.5") // in cloudnet but not exposed
    compileOnly("org.ow2.asm:asm-tree:9.5") // in cloudnet but not exposed
    api(darkcubesystem.server)
    api(libs.bundles.minestom)
}