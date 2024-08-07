/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    id("eu.darkcube.darkcube")
}

dependencies {
    api(darkcubesystem.server)
    implementation(projects.woolbattle.provider)
}