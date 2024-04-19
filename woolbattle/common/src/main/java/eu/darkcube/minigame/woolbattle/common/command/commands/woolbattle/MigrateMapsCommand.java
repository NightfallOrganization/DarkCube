package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.util.data.Key;

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
                                var materialKey = new Key("minecraft", materialName);
                                Material material;
                                try {
                                    material = Material.of(materialKey);
                                } catch (IllegalArgumentException e) {
                                    material = woolbattle.materialProvider().grassBlock();
                                }
                                var icon = ItemBuilder.item(material);
                                map.icon(icon);
                            }
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