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
    api(parent!!.project("adventure-platform-facet"))
    api(parent!!.project("adventure-text-serializer-gson"))
    compileOnly("io.netty:netty-all:4.0.23.Final")
    compileOnlyApi(libs.viaversion)
}
