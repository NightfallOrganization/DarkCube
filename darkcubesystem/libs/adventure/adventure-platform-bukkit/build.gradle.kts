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
    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT") {
        exclude(group = "com.google.code.gson")
    }

    compileOnly("io.netty:netty-all:4.0.23.Final")
    api(parent!!.project("adventure-platform-viaversion"))
    api(parent!!.dependencies.project("adventure-text-serializer-gson-legacy-impl")) {
        exclude(group = "com.google.code.gson")
    }
    api(parent!!.project("adventure-text-serializer-legacy"))
    api(parent!!.dependencies.project("adventure-text-serializer-bungeecord")) {
        exclude(group = "com.google.code.gson")
    }
}
