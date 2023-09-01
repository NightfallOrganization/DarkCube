/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.gradle.jvm.tasks.Jar

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    id("java-library")
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
    val compileOnly = compileClasspath
    named("nodeCompileOnly") {
        extendsFrom(compileOnly.get())
    }
    named("wrapperCompileOnly") {
        extendsFrom(compileOnly.get())
    }
}

dependencies {
    compileOnly(libs.cloudnet.driver)
    compileOnly(libs.cloudnet.bridge)
    api(projects.darkcubesystem.implementation.common)

    "nodeCompileOnly"(libs.cloudnet.node)
    "nodeCompileOnly"(sourceSets.getByName("main").output)

    "wrapperCompileOnly"(libs.cloudnet.wrapper)
    "wrapperCompileOnly"(sourceSets.getByName("main").output)
}
