plugins {
    id("java-library")
}

tasks.withType<Jar>().configureEach {
    archiveBaseName = "woolbattle"
}

dependencies {
    compileOnlyApi(project(":darkcubesystem:bukkit"))
    compileOnlyApi(project(":bukkit:statsapi"))
    compileOnly(project(":pserver:pserver-api"))
    compileOnly("io.papermc.paper:paper:1.8.8-R0.1-SNAPSHOT")
    compileOnlyApi(libs.cloudnetBridge)
    compileOnlyApi(libs.cloudnetWrapper)
}
