/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("java-library")
}

dependencies {
    compileOnly(libs.cloudnet.node)
    compileOnly(libs.cloudnet.bridge)
    compileOnly(rootProject.project("darkcubesystem"))
}
