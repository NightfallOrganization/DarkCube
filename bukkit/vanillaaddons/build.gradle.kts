/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar.configure {
        archiveClassifier = ""
        relocate("net.wesjd.anvilgui", "eu.darkcube.system.vanillaaddons.libs.net.wesjd.anvilgui")
    }
    jar.configure {
        archiveClassifier = "pure"
    }
    compileJava {
        options.release = 17
    }
    assemble.configure {
        dependsOn(shadowJar)
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly(project(":darkcubesystem"))
    implementation("net.wesjd:anvilgui:1.7.0-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

repositories {
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}
