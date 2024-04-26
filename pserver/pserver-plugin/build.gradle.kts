/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(project(":pserver:pserver-bukkit"))
    compileOnly(project(":darkcubesystem:bukkit"))
    compileOnly(project(":woolbattle:bukkit"))
    compileOnly(libs.luckperms)
}
