/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.gradle.jvm.tasks.Jar

plugins {
    `java-library`
}

sourceSets {
    register("node") {}
    register("wrapper") {}
}

tasks {
    named<Jar>("jar") {
        from(sourceSets.getByName("node").output)
        from(sourceSets.getByName("wrapper").output)
        dependsOn(named("nodeClasses"))
        dependsOn(named("wrapperClasses"))
    }
}

configurations {
    named("nodeCompileOnly") {
        extendsFrom(compileClasspath.get())
    }
    named("wrapperCompileOnly") {
        extendsFrom(compileClasspath.get())
    }
}

dependencies {
    compileOnly(libs.cloudnet.driver)
    compileOnly(libs.cloudnet.bridge)
    api(projects.darkcubesystem.implementation.common)

    "nodeCompileOnly"(libs.cloudnet.node)
    "nodeCompileOnly"(sourceSets.main.map { it.output })

    "wrapperCompileOnly"(libs.cloudnet.wrapper)
    "wrapperCompileOnly"(sourceSets.main.map { it.output })
}
