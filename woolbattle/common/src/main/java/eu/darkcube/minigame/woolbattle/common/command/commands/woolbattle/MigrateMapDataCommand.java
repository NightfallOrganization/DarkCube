package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.driver.template.TemplateStorage;
import eu.cloudnetservice.driver.template.TemplateStorageProvider;
import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.map.CommonMapManager;
import eu.darkcube.minigame.woolbattle.common.util.GsonUtil;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.util.AsyncExecutor;

public class MigrateMapDataCommand extends WoolBattleCommand {
    public MigrateMapDataCommand(CommonWoolBattleApi woolbattle) {
        super("migrateMapData", b -> b.executes(ctx -> {
            ctx.getSource().sendMessage(Component.text("Migrating map data..."));
            var templateStorageProvider = InjectionLayer.boot().instance(TemplateStorageProvider.class);
            templateStorageProvider.availableTemplateStoragesAsync().thenAccept(storages -> {
                if (!storages.contains("woolbattle")) {
                    ctx.getSource().sendMessage(Component.text("Missing legacy storage \"woolbattle\""));
                    return;
                }
                AsyncExecutor.cachedService().submit(() -> {
                    migrate(woolbattle, ctx.getSource(), templateStorageProvider.templateStorage("woolbattle"));
                    ctx.getSource().sendMessage(Component.text("Migration complete"));
                });
            });
            return 0;
        }));
    }

    private static void migrate(CommonWoolBattleApi woolbattle, CommandSource source, TemplateStorage storage) {
        for (var map : woolbattle.mapManager().maps()) {
            try {
                migrate(woolbattle.mapManager(), map, source, storage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void migrate(CommonMapManager mapManager, Map map, CommandSource source, TemplateStorage storage) throws IOException {
        var template = legacyTemplate(map);
        if (!storage.contains(template)) {
            source.sendMessage(Component.text("No world exists for " + template));
            return;
        }
        source.sendMessage(Component.text("Migrating " + template));
        var zipIn = storage.openZipInputStream(template);
        if (zipIn == null) {
            source.sendMessage(Component.text("Failed to migrate " + map.name() + "-" + map.size() + ": Zip was null"));
            return;
        }
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
                        var y = teamJson.get("y").getAsDouble();
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
                source.sendMessage(Component.text("Unable to find data.json for " + map.name() + "-" + map.size()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mapManager.saveIngameData(ingameData);
    }

    private static ServiceTemplate legacyTemplate(Map map) {
        return ServiceTemplate.builder().prefix(map.size().toString()).name(map.name()).storage("woolbattle").build();
    }
}
