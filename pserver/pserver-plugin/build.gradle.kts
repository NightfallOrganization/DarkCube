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
