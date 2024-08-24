/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.driver.template.TemplateStorage;
import eu.cloudnetservice.driver.template.TemplateStorageProvider;
import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.map.CommonMap;
import eu.darkcube.minigame.woolbattle.common.map.CommonMapManager;
import eu.darkcube.minigame.woolbattle.common.util.GsonUtil;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.util.AsyncExecutor;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class MigrateMapsCommand extends WoolBattleCommand {

    private static final Object2ObjectMap<String, String[]> mappings = new Object2ObjectOpenHashMap<>();
    private static final String[] colors = new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
    private static final List<Pair<String, Integer>> failed = new ArrayList<>();

    static {
        r("ladder");
        r("nether_star");
        r("prismarine_crystals");
        r("snow_layer", "snow");
        r("snowball");
        r("stonebrick", "stone_bricks");
        rc("wool", "wool");
        rc("stained_hardened_clay", "terracotta");
        r("log", 2, "birch_log");
        r("record_stal", "music_disc_stal");
        r("milk_bucket");
        r("brick_stairs", "stone_brick_stairs");
        r("ender_eye");
        r("string");
        r("nether_brick_stairs");
        r("comparator");
        r("daylight_detector");
        r("spruce_stairs");
        r("slime_ball");
        r("prismarine_shard");
        r("glass_pane");
        r("stone_pressure_plate");
        r("cobblestone_wall");
        r("hardened_clay", "terracotta");
        r("hopper_minecart");
        r("acacia_stairs");
        r("quartz_ore", "nether_quartz_ore");
        r("log", 0, "oak_log");
        r("armor_stand");
        r("item_frame");
        r("quartz_block", 2, "quartz_pillar");
        r("ender_pearl");
        r("dark_oak_fence");
        rc("carpet", "carpet");
        r("cookie");
        r("lever");
        r("stone");
        r("firework_charge", "firework_star");
        r("magma_cream");
        r("stone", 2, "polished_granite");
        r("stone_slab", 0, "smooth_stone_slab");
        r("stone_slab", 3, "cobblestone_slab");
        r("emerald");
        r("hopper");
        r("crafting_table");
        r("fence", "oak_fence");
        r("beacon");
        r("red_flower", "poppy");
        r("stone_slab", 6, "nether_brick_slab");
        r("clay");
        r("tripwire_hook");
        r("grass", "grass_block");
        r("flower_pot");
    }

    private static void rc(String id, String name) {
        for (var i = 0; i < colors.length; i++) {
            r(id, i, colors[i] + "_" + name);
        }
    }

    private static void r(String id) {
        r(id, id);
    }

    private static void r(String id, String name) {
        r(id, 0, name);
    }

    private static void r(String id, int data, String name) {
        if (data >= 16 || data < 0) throw new IllegalArgumentException();
        mappings.computeIfAbsent(id, _ -> new String[16])[data] = name;
    }

    private static final String[] EMPTY = new String[16];

    private static @Nullable String mapping(String name, int damage) {
        var mapping = mappings.getOrDefault(name, EMPTY)[damage];
        if (mapping == null) {
            var pair = Pair.of(name, damage);
            if (!failed.contains(pair)) {
                failed.add(pair);
                System.out.println("Missing: " + name + ":" + damage);
            }
        }
        return mapping;
    }

    public MigrateMapsCommand(CommonWoolBattleApi woolbattle) {
        super("migrateMaps", b -> b.executes(ctx -> {
            ctx.getSource().sendMessage(Messages.MIGRATING_MAPS);
            migrateMaps(woolbattle, ctx.getSource());
            return 0;
        }));
    }

    private static void migrateMaps(CommonWoolBattleApi api, CommandSource source) {
        var provider = InjectionLayer.boot().instance(DatabaseProvider.class);
        if (!provider.containsDatabase("woolbattle_maps")) {
            source.sendMessage(Messages.NO_MAPS_TO_MIGRATE);
            return;
        }
        var database = provider.database("woolbattle_maps");
        database.entriesAsync().thenAccept(entries -> {
            var deathHeights = new HashMap<CommonMap, Integer>();
            try {
                var errors = new HashMap<MapSize, List<Map.Entry<String, String>>>();
                for (var entry : entries.entrySet()) {
                    var data = entry.getValue();
                    var deathHeight = data.getInt("deathHeight");
                    var enabled = data.getBoolean("enabled");
                    var name = data.getString("name");
                    var iconString = data.getString("icon");
                    var mapSizeDocument = data.readDocument("mapSize");
                    var teams = mapSizeDocument.getInt("teams");
                    var teamSize = mapSizeDocument.getInt("teamSize");
                    var mapSize = new MapSize(teams, teamSize);
                    var oldMap = api.mapManager().map(name, mapSize);
                    if (oldMap != null) {
                        api.mapManager().deleteMap(oldMap);
                    }

                    var map = api.mapManager().createMap(name, mapSize);
                    deathHeights.put(map, deathHeight);
                    map.enabled(enabled);

                    {
                        String oldId = null;
                        var oldDamage = 0;
                        iconString = iconString.substring(1, iconString.length() - 1); // Remove {}
                        for (var split : iconString.split(",")) {
                            if (split.startsWith("id:\"minecraft:") && split.endsWith("\"")) {
                                oldId = split.substring(14, split.length() - 1);
                                // var materialKey = Key.key("minecraft", materialName);
                                // Material material;
                                // try {
                                //     material = Material.of(materialKey);
                                // } catch (IllegalArgumentException e) {
                                //     errors.computeIfAbsent(mapSize, _ -> new ArrayList<>()).add(Map.entry(name, iconString));
                                //     material = api.materialProvider().grassBlock();
                                // }
                                // var icon = ItemBuilder.item(material);
                                // map.icon(icon);
                            } else if (split.startsWith("Damage:") && split.endsWith("s")) {
                                oldDamage = Integer.parseInt(split.substring(7, split.length() - 1));
                            }
                        }
                        if (oldId != null) {
                            var mapping = mapping(oldId, oldDamage);
                            if (mapping != null) {
                                map.icon(ItemBuilder.item(Material.of(Key.key(mapping))));
                            } else {
                                errors.computeIfAbsent(mapSize, _ -> new ArrayList<>()).add(Map.entry(name, iconString));
                            }
                        }
                    }
                }
                for (var entry : errors.entrySet()) {
                    var size = entry.getKey();
                    for (var e : entry.getValue()) {
                        var name = e.getKey();
                        var icon = e.getValue();
                        source.sendMessage(text("ERROR: " + name + "-" + size + ": " + icon));
                    }
                }
                source.sendMessage(Messages.MAP_MIGRATION_COMPLETE);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            migrate(api, source, deathHeights);
        });
    }

    private static void migrate(CommonWoolBattleApi api, CommandSource source, Map<CommonMap, Integer> deathHeights) {
        source.sendMessage(text("Migrating map data..."));
        var templateStorageProvider = InjectionLayer.boot().instance(TemplateStorageProvider.class);
        templateStorageProvider.availableTemplateStoragesAsync().thenAccept(storages -> {
            if (!storages.contains("woolbattle")) {
                source.sendMessage(text("Missing legacy storage \"woolbattle\""));
                return;
            }
            AsyncExecutor.cachedService().submit(() -> {
                migrate(api, source, templateStorageProvider.templateStorage("woolbattle"), deathHeights);
                source.sendMessage(text("Migration complete"));
            });
        });
    }

    private static void migrate(CommonWoolBattleApi woolbattle, CommandSource source, TemplateStorage storage, Map<CommonMap, Integer> deathHeights) {
        for (var map : woolbattle.mapManager().maps()) {
            try {
                migrate(woolbattle.mapManager(), map, source, storage, deathHeights.get(map));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void migrate(CommonMapManager mapManager, eu.darkcube.minigame.woolbattle.api.map.Map map, CommandSource source, TemplateStorage storage, int deathHeight) throws IOException {
        var template = legacyTemplate(map);
        if (!storage.contains(template)) {
            source.sendMessage(text("No world exists for " + template));
            return;
        }
        source.sendMessage(text("Migrating " + template));
        var zipIn = storage.openZipInputStream(template);
        if (zipIn == null) {
            source.sendMessage(text("Failed to migrate " + map.name() + "-" + map.size() + ": Zip was null"));
            return;
        }
        var offsetY = mapManager.deathHeight() - deathHeight;
        var ingameData = mapManager.loadIngameData(map);
        try {
            var foundDataJson = false;
            while (true) {
                var entry = zipIn.getNextEntry();
                if (entry == null) break;
                try {
                    var name = entry.getName();
                    if (!name.equals("data.json")) continue;
                    foundDataJson = true;
                    var bytes = zipIn.readAllBytes();
                    var json = GsonUtil.gson().fromJson(new String(bytes, StandardCharsets.UTF_8), JsonObject.class);
                    var jsonSpawns = json.getAsJsonObject("spawns");
                    for (var teamName : jsonSpawns.keySet()) {
                        var teamJson = jsonSpawns.getAsJsonObject(teamName);
                        var x = teamJson.get("x").getAsDouble();
                        var y = teamJson.get("y").getAsDouble() + offsetY;
                        var z = teamJson.get("z").getAsDouble();
                        var yaw = teamJson.get("yaw").getAsFloat();
                        var pitch = teamJson.get("pitch").getAsFloat();
                        var pos = new Position.Directed.Simple(x, y, z, yaw, pitch);

                        ingameData.spawn(teamName, pos);
                    }
                } finally {
                    zipIn.closeEntry();
                }
            }
            zipIn.close();
            if (!foundDataJson) {
                source.sendMessage(text("Unable to find data.json for " + map.name() + "-" + map.size()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mapManager.saveIngameData(ingameData);
    }

    private static ServiceTemplate legacyTemplate(eu.darkcube.minigame.woolbattle.api.map.Map map) {
        return ServiceTemplate.builder().prefix(map.size().toString()).name(map.name()).storage("woolbattle").build();
    }
}
