/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("java-library")
}

dependencies {
    api(parent!!.project("adventure-text-serializer-gson"))
    api(parent!!.project("adventure-text-serializer-legacy"))
    compileOnly(libs.bungee)
}
