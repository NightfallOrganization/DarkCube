/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("java-library")
}

val embed = configurations.create("embed")

dependencies {
    embed(libs.bundles.adventure)
    embed(libs.brigadier)
    embed(libs.gson)
    embed(libs.annotations)
}

sourceRemapper.remap(embed, "eu.darkcube.system.libs", configurations.named("api"))
