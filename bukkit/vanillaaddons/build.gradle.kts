/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

tasks {
    shadowJar.configure {
        archiveClassifier = ""
        relocate("net.wesjd.anvilgui", "eu.darkcube.system.vanillaaddons.libs.net.wesjd.anvilgui")
    }
    jar.configure {
        archiveClassifier = "pure"
    }
    assemble.configure {
        dependsOn(shadowJar)
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly(project(":darkcubesystem"))
    implementation("net.wesjd:anvilgui:1.9.3-SNAPSHOT")
}

repositories {
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}
