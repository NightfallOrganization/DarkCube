/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    java
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

sourceSets {
    create("generator") {
    }
}

tasks.withType<JavaExec>().configureEach {
    if (name.endsWith("main()")) {
        notCompatibleWithConfigurationCache("JavaExec created by IntelliJ")
    }
}

dependencies {
    "generatorImplementation"("com.google.code.gson:gson:2.10.1")
    "generatorImplementation"("it.unimi.dsi:fastutil:8.5.12")
    compileOnly("it.unimi.dsi:fastutil:8.5.12")
    testRuntimeOnly("it.unimi.dsi:fastutil:8.5.12")
}