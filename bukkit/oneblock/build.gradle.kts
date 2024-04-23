/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    java
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly(project(":darkcubesystem"))
    compileOnly(parent!!.project("luckperms-prefixplugin"))
    compileOnly("io.github.juliarn", "npc-lib-api", "3.0.0-beta6")
    compileOnly("io.github.juliarn", "npc-lib-common", "3.0.0-beta6")
    implementation("io.github.juliarn", "npc-lib-bukkit", "3.0.0-beta6")
}
