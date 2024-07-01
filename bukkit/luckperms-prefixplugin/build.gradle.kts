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
val versions = ArrayList<String>()

register("mc1_8_R3", "1.8.8")
register("mc1_13_R2", "1.13.2")
register("mc1_14_R1", "1.14.4", "mc1_13_R2")
register("mc1_15_R1", "1.15.2", "mc1_13_R2")
register("mc1_16_R3", "1.16.3", "mc1_13_R2")
register("mc1_19_R2", "1.19.3", "mc1_13_R2", true)
register("mc1_20_R1", "1.20.1", "mc1_13_R2", true)
register("mc1_20_R2", "1.20.2", "mc1_13_R2", true)
register("mc1_20_R3", "1.20.4", "mc1_13_R2", true)
register("mc1_20_6", "1.20.6", "mc1_13_R2", true)

val generateVersionsMeta = tasks.register("generateVersionsMeta") {
    inputs.property("versions", versions.toString())
    val file = temporaryDir.resolve("prefix.versions")
    val v = versions
    doLast {
        file.writeText(v.joinToString(separator = "\n"))
    }
    outputs.file(file)
}
tasks.jar.configure {
    sourceSets.all {
        from(output)
    }
    from(generateVersionsMeta)
}

dependencies {
    compileOnly(libs.paper.latest)
    compileOnly(darkcubesystem.api)
    compileOnly(libs.luckperms)
}

fun register(name: String, version: String, depend: String? = null, paper: Boolean = false) {
    versions.add("v" + name.substring(2))
    sourceSets.register(name)

    dependencies {
        "${name}CompileOnly"(sourceSets["main"].output)
        "${name}CompileOnly"(api(paper, version))
        if (depend != null) {
            "${name}CompileOnly"(sourceSets[depend].output)
        }
    }
}

fun api(paper: Boolean, version: String): String {
    if (paper) {
        return "io.papermc.paper:paper-api:$version-R0.1-SNAPSHOT"
    }
    return "org.spigotmc:spigot-api:$version-R0.1-SNAPSHOT"
}
