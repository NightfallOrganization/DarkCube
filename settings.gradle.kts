/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

/**
 * @see <a href="https://github.com/gradle/gradle/issues/19069">Feature request</a>
 */
fun Settings.enableFeaturePreviewQuietly(name: String, summary: String = name) {
    enableFeaturePreview(name)

    val logger: Any = org.gradle.util.internal.IncubationLogger::class.java
        .getDeclaredField("INCUBATING_FEATURE_HANDLER")
        .apply { isAccessible = true }
        .get(null)

    @Suppress("UNCHECKED_CAST")
    val features: MutableSet<String> = org.gradle.internal.featurelifecycle.LoggingIncubatingFeatureHandler::class.java
        .getDeclaredField("features")
        .apply { isAccessible = true }
        .get(logger) as MutableSet<String>

    features.add(summary)
}

pluginManagement {
    includeBuild("build-extensions")
    repositories {
        gradlePluginPortal()
        maven("https://nexus.darkcube.eu/repository/darkcube/") {
            name = "DarkCube"
            credentials(PasswordCredentials::class)
        }
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}

plugins {
    id("eu.darkcube.darkcube.settings") version "1.1.5"
}

rootProject.name = "DarkCube"

includeBuild("minestom/minestom-library/Minestom")

include("bukkit")
include("bukkit:autovoidworld")
include("bukkit:bauserver")
//include "bukkit:bedwars"
include("bukkit:changepermissionmessage")

includeSubProjects("minestom", "server")

includeSubProjects("woolbattle", "bukkit", "api", "minestom")

include("bukkit:darkessentials")
//include "bukkit:holograms"
include("bukkit:jumpleague")
//include "bukkit:knockout"
//include("bukkit:aetheria")
include("bukkit:oneblock")
include("bukkit:woolbattleteamfight")
include("bukkit:jumpleaguemodules")
include("bukkit:lobbysystem")
include("bukkit:luckperms-prefixplugin")
include("bukkit:miners")
include("bukkit:skyland")
include("bukkit:vanillaaddons")
include("bukkit:sumo")
//include "bukkit:smash"
include("bukkit:statsapi")
//include("bungee")
//include("bungee:cmd-bauserver")
//include("bungee:friendsystem")
//include("bungee:partysystem")
//include("bungee:werbung")
//include "cloudban"
//include "cloudban:cloudban-bukkit"
//include "cloudban:cloudban-bungee"
//include "cloudban:cloudban-common"
//include "cloudban:cloudban-module"
//include("cloudnet")
include("cloudnet:service-helper")
//include("cloudnet:cloudnet-database-mysql")
//include("cloudnet:stats-cloudnet")
include("common")
include("common:labymod-emotes")
include("common:glyph-width-loader")
include("pserver")
include("pserver:pserver-api")
include("pserver:pserver-bukkit")
include("pserver:pserver-cloudnet")
include("pserver:pserver-plugin")
//includeSubProjects("replay", "api", "module")
//includeSubProjects("replay:bukkit", "1.8.8")

fun includeSubProjects(rootProject: String, vararg subProjects: String) {
    include(rootProject)
    for (subProject in subProjects) {
        val path = ":$rootProject:$subProject"
        include(path)
        var name = project(path).name
        if (name.startsWith("1")) name = "v$name".replace(".", "_")
        project(path).name = name
    }
}
