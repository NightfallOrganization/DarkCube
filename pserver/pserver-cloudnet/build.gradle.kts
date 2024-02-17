plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

tasks {
    jar {
        destinationDirectory = temporaryDir
    }
    shadowJar {
        archiveClassifier = null
    }
    assemble {
        dependsOn(shadowJar)
    }
}

dependencies {
    compileOnlyApi(libs.cloudnet.node)
    compileOnlyApi(libs.cloudnet.bridge)
//    compileOnlyApi project(':darkcubesystem:module')
    api(project(":pserver:pserver-api"))
    runtimeOnly(project(":pserver:pserver-bukkit"))
}
