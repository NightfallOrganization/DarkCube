/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    java
    id("eu.darkcube.darkcube")
}

sourceSets {
    register("mc1_8_R3")
    register("mc1_13_R2")
    register("mc1_14_R1")
    register("mc1_15_R1")
    register("mc1_16_R3")
    register("mc1_19_R2")
    register("mc1_20_R1")
    register("mc1_20_R2")
    register("mc1_20_R3")
}
tasks.jar.configure {
    sourceSets.all {
        from(output)
    }
}

dependencies {
    "mc1_8_R3CompileOnly"(sourceSets["main"].output)
    "mc1_8_R3CompileOnly"("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    "mc1_13_R2CompileOnly"(sourceSets["main"].output)
    "mc1_13_R2CompileOnly"("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    "mc1_14_R1CompileOnly"(sourceSets["main"].output)
    "mc1_14_R1CompileOnly"(sourceSets["mc1_13_R2"].output)
    "mc1_14_R1CompileOnly"("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    "mc1_15_R1CompileOnly"(sourceSets["main"].output)
    "mc1_15_R1CompileOnly"(sourceSets["mc1_13_R2"].output)
    "mc1_15_R1CompileOnly"("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    "mc1_16_R3CompileOnly"(sourceSets["main"].output)
    "mc1_16_R3CompileOnly"(sourceSets["mc1_13_R2"].output)
    "mc1_16_R3CompileOnly"("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
    "mc1_19_R2CompileOnly"(sourceSets["main"].output)
    "mc1_19_R2CompileOnly"(sourceSets["mc1_13_R2"].output)
    "mc1_19_R2CompileOnly"("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    "mc1_20_R1CompileOnly"(sourceSets["main"].output)
    "mc1_20_R1CompileOnly"(sourceSets["mc1_13_R2"].output)
    "mc1_20_R1CompileOnly"("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    "mc1_20_R2CompileOnly"(sourceSets["main"].output)
    "mc1_20_R2CompileOnly"(sourceSets["mc1_13_R2"].output)
    "mc1_20_R2CompileOnly"("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    "mc1_20_R3CompileOnly"(sourceSets["main"].output)
    "mc1_20_R3CompileOnly"(sourceSets["mc1_13_R2"].output)
    "mc1_20_R3CompileOnly"("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(darkcubesystem.api)
    compileOnly(libs.luckperms)
}
