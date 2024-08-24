/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    application
    id("eu.darkcube.darkcube")
}

application {
    mainClass = "eu.darkcube.minigame.woolbattle.converter.Main"

}

tasks.withType<JavaExec> {
    workingDir = projectDir
}

dependencies {
    implementation(projects.woolbattle.common)
    implementation(libs.fastutil)
}