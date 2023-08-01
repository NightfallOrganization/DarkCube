plugins {
    id("com.github.johnrengelman.shadow")
    id("java-library")
}

tasks {
    shadowJar.configure {
        archiveClassifier = ""
    }
    jar.configure {
        archiveClassifier = "pure"
    }
    assemble.configure { dependsOn(shadowJar) }
}

dependencies {
    api(project("api"))
    runtimeOnly(project("bukkit"))
    runtimeOnly(project("module"))
}
