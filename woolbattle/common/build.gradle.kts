/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    id("eu.darkcube.darkcube")
}

dependencies {
    implementation(projects.woolbattle.provider)
    api(projects.woolbattle.api)
    api(projects.pserver.pserverApi)
    api(darkcubesystem.server.cloudnet)
    api(libs.jctools.core)
    api(libs.fastutil)
}