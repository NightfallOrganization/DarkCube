/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.material.Material;

public class MigrateMapsCommand extends WoolBattleCommand {
    public MigrateMapsCommand(CommonWoolBattleApi woolbattle) {
        super("migrateMaps", b -> b.executes(ctx -> {
            ctx.getSource().sendMessage(Messages.MIGRATING_MAPS);
            var provider = InjectionLayer.boot().instance(DatabaseProvider.class);
            if (!provider.containsDatabase("woolbattle_maps")) {
                ctx.getSource().sendMessage(Messages.NO_MAPS_TO_MIGRATE);
                return 0;
            }
            var database = provider.database("woolbattle_maps");
            database.entriesAsync().thenAccept(entries -> {
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
                        var oldMap = woolbattle.mapManager().map(name, mapSize);
                        if (oldMap != null) {
                            woolbattle.mapManager().deleteMap(oldMap);
                        }

                        var map = woolbattle.mapManager().createMap(name, mapSize);
                        map.deathHeight(deathHeight);
                        map.enabled(enabled);

                        iconString = iconString.substring(1, iconString.length() - 1); // Remove {}
                        for (var split : iconString.split(",")) {
                            if (split.startsWith("id:\"minecraft:") && split.endsWith("\"")) {
                                var materialName = split.substring(14, split.length() - 1);
                                var materialKey = Key.key("minecraft", materialName);
                                Material material;
                                try {
                                    material = Material.of(materialKey);
                                } catch (IllegalArgumentException e) {
                                    errors.computeIfAbsent(mapSize, _ -> new ArrayList<>()).add(Map.entry(name, iconString));
                                    material = woolbattle.materialProvider().grassBlock();
                                }
                                var icon = ItemBuilder.item(material);
                                map.icon(icon);
                            }
                        }
                    }
                    for (var entry : errors.entrySet()) {
                        var size = entry.getKey();
                        for (var e : entry.getValue()) {
                            var name = e.getKey();
                            var icon = e.getValue();
                            ctx.getSource().sendMessage(Component.text("ERROR: " + name + "-" + size + ": " + icon));
                        }
                    }
                    ctx.getSource().sendMessage(Messages.MAP_MIGRATION_COMPLETE);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
            return 0;
        }));
    }
}
