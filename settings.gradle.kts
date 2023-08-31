/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-extensions")
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}

rootProject.name = "DarkCube"
include("bukkit")
include("bukkit:autovoidworld")
include("bukkit:bauserver")
//include "bukkit:bedwars"
include("bukkit:changepermissionmessage")
includeSubProjects("darkcubesystem", "api", "module", "velocity", "common")
includeSubProjects("darkcubesystem:libs", "brigadier", "gson", "annotations")
includeSubProjects("darkcubesystem:libs:adventure", "adventure-api", "adventure-key", "adventure-nbt", "adventure-platform-api", "adventure-platform-bukkit", "adventure-platform-facet", "adventure-platform-viaversion", "adventure-text-serializer-bungeecord", "adventure-text-serializer-gson", "adventure-text-serializer-gson-legacy-impl", "adventure-text-serializer-legacy", "adventure-text-serializer-plain", "examination-api", "examination-string")
includeSubProjects("darkcubesystem:bukkit", "1.8.8", "1.20.1")

includeSubProjects("woolbattle", "bukkit", "api", "minestom")

//includeSubProjects("darkcubesystem", "1.8.8", "1.19.3", "core", "module")
include("bukkit:darkessentials")
//include "bukkit:holograms"
include("bukkit:jumpleague")
//include "bukkit:knockout"
include("bukkit:citybuild")
include("bukkit:lobbysystem")
include("bukkit:luckperms-prefixplugin")
include("bukkit:miners")
include("bukkit:skyland")
include("bukkit:vanillaaddons")
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
        include("$rootProject:$subProject")
    }
}
