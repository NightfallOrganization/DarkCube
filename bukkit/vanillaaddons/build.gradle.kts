/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    alias(libs.plugins.shadow)
    id("eu.darkcube.darkcube")
}

tasks {
    shadowJar.configure {
        archiveClassifier = ""
        relocate("net.wesjd.anvilgui", "eu.darkcube.system.vanillaaddons.libs.net.wesjd.anvilgui")
    }
    jar.configure {
        destinationDirectory = temporaryDir
    }
    assemble.configure {
        dependsOn(shadowJar)
    }
}

dependencies {
    compileOnly(libs.paper.latest)
    compileOnly(darkcubesystem.bukkit)
    implementation("net.wesjd:anvilgui:1.9.4-SNAPSHOT")
}
repositories {
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}
