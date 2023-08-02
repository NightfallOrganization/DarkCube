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
    api(project("adventure-api"))
    api(project("adventure-text-serializer-bungeecord")) {
        exclude(group = "com.google.code.gson")
    }
    api(project("adventure-text-serializer-gson")) {
        exclude(group = "com.google.code.gson")
    }
    api(project("adventure-text-serializer-gson-legacy-impl")) {
        exclude(group = "com.google.code.gson")
    }
    api(project("adventure-text-serializer-legacy"))
    api(project("adventure-text-serializer-plain"))
    api(project("adventure-platform-bukkit")) {
        exclude(group = "com.google.code.gson")
    }
    runtimeOnly(project("adventure-platform-viaversion")) {
        exclude(group = "com.google.code.gson")
    }
}

subprojects {
    tasks.withType<Javadoc>().configureEach {
        (options as StandardJavadocDocletOptions).tags = listOf("sinceMinecraft")
    }
}
