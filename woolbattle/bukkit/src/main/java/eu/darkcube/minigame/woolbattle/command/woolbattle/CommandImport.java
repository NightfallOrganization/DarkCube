/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.CloudTemplateArgument;
import eu.darkcube.minigame.woolbattle.map.CloudNetMapIngameData;
import eu.darkcube.minigame.woolbattle.map.DefaultMap;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.MaterialAndId;
import eu.darkcube.minigame.woolbattle.util.UnloadedLocation;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.voidworldplugin.VoidWorldPlugin;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class CommandImport extends WBCommandExecutor {
    public CommandImport(WoolBattleBukkit woolbattle) {
        super("import", b -> b.then(Commands.argument("template", CloudTemplateArgument.template()).executes(ctx -> {
            ServiceTemplate template = CloudTemplateArgument.template(ctx, "template");
            Path directory = woolbattle
                    .getDataFolder()
                    .toPath()
                    .resolve("importing")
                    .resolve(template.getPrefix())
                    .resolve(template.getName());
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ctx.getSource().sendMessage(Component.text("Starting download of template"));
            CloudNetDriver.getInstance().getLocalTemplateStorage().copyAsync(template, directory).onComplete(suc -> {
                if (suc) {
                    ctx.getSource().sendMessage(Component.text("Download of template successful"));
                    new Scheduler(woolbattle) {
                        @Override public void run() {
                            try {
                                startImport(woolbattle, ctx, directory);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    }.runTask();
                } else {
                    ctx.getSource().sendMessage(Component.text("Download failed"));
                }
            });
            return 0;
        })));
    }

    private static void startImport(WoolBattleBukkit woolbattle, CommandContext<CommandSource> ctx, Path directory) throws IOException {
        Path plugins = directory.resolve("plugins");
        Path WoolBattle = plugins.resolve("WoolBattle");
        Path teamsYml = WoolBattle.resolve("teams.yml");
        if (!Files.exists(teamsYml)) {
            ctx.getSource().sendMessage(Component.text("No teams.yml found. Invalid template!"));
            return;
        }
        Path spawnsYml = WoolBattle.resolve("spawns.yml");
        if (!Files.exists(spawnsYml)) {
            ctx.getSource().sendMessage(Component.text("No spawns.yml found. Invalid template!"));
            return;
        }
        ctx.getSource().sendMessage(Component.text("Importing teams"));
        MapSize mapSize = importTeams(ctx, woolbattle, teamsYml);
        ctx.getSource().sendMessage(Component.text("Importing spawns"));
        importSpawns(ctx, woolbattle, directory, spawnsYml, mapSize);
        ctx.getSource().sendMessage(Component.text("Import complete"));
        deleteDirectory(directory);
    }

    private static void importSpawns(CommandContext<CommandSource> ctx, WoolBattleBukkit woolbattle, Path directory, Path spawnsYml, MapSize mapSize) throws IOException {
        YamlConfiguration spawns = YamlConfiguration.loadConfiguration(Files.newBufferedReader(spawnsYml, StandardCharsets.UTF_8));
        // DefaultLocation isn't used anymore
        // Importing lobby isn't smart because of the new system
        // Importing lobbydeathline same reason as lobby
        // Only maps left

        String mapsString = spawns.getString("maps");
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(mapsString, JsonObject.class);
        for (String key : root.keySet()) {
            JsonObject json = root.get(key).getAsJsonObject();
            String name = json.get("name").getAsString();
            int deathHeight = json.get("deathHeight").getAsInt();
            boolean enabled = json.get("enabled").getAsBoolean();
            JsonObject iconJson = json.get("icon").getAsJsonObject();
            MaterialAndId oldIcon = new MaterialAndId(Material.valueOf(iconJson.get("material").getAsString()), iconJson
                    .get("id")
                    .getAsByte());
            ItemStack icon = ItemBuilder.item(oldIcon.getMaterial()).damage(oldIcon.getId()).build();
            JsonObject spawnsJson = json.get("spawns").getAsJsonObject();
            DefaultMap map = (DefaultMap) woolbattle.mapManager().createMap(name, mapSize);
            map.deathHeight(deathHeight);
            map.setIcon(icon);
            if (enabled) map.enable();
            String originalName = null;
            CloudNetMapIngameData ingameData = new CloudNetMapIngameData();
            ingameData.worldName(map.getName() + "-" + map.size());
            for (String nameKey : spawnsJson.keySet()) {
                String locString = spawnsJson.get(nameKey).getAsString();
                String[] a = locString.split(":");
                String X = a[0];
                String Y = a[1];
                String Z = a[2];
                String Yaw = a[3];
                String Pitch = a[4];
                originalName = a[5];
                double x = Double.parseDouble(X);
                double y = Double.parseDouble(Y);
                double z = Double.parseDouble(Z);
                float yaw = Float.parseFloat(Yaw);
                float pitch = Float.parseFloat(Pitch);
                UnloadedLocation loc = new UnloadedLocation(x, y, z, yaw, pitch, ingameData.worldName());
                ingameData.spawn(nameKey.toLowerCase(Locale.ROOT), loc);
            }

            Path target = Bukkit.getWorldContainer().toPath().normalize().toAbsolutePath().resolve(ingameData.worldName()).toAbsolutePath();
            ctx.getSource().sendMessage(Component.text("Copy world " + ingameData.worldName() + " to " + target.toAbsolutePath()));
            Path worldDirectory = directory.resolve(originalName);
            Files.walkFileTree(worldDirectory, new SimpleFileVisitor<Path>() {
                @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(target.resolve(worldDirectory.relativize(dir).toString()));
                    return FileVisitResult.CONTINUE;
                }

                @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(worldDirectory.relativize(file).toString()));
                    return FileVisitResult.CONTINUE;
                }
            });
            World world = VoidWorldPlugin.instance().loadWorld(ingameData.worldName());
            ingameData.world(world);
            map.ingameData(ingameData);
            Bukkit.unloadWorld(world, false);
            woolbattle.mapLoader().save(map).thenRun(() -> {
                map.ingameData(null);
                try {
                    deleteDirectory(target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static void deleteDirectory(Path path) throws IOException {
        try (Stream<Path> walk = Files.walk(path)) {
            //noinspection ResultOfMethodCallIgnored
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    private static MapSize importTeams(CommandContext<CommandSource> ctx, WoolBattleBukkit woolbattle, Path teamsYml) throws IOException {
        YamlConfiguration teams = YamlConfiguration.loadConfiguration(Files.newBufferedReader(teamsYml, StandardCharsets.UTF_8));
        List<String> teamsList = teams.getStringList("teams");
        Gson gson = new Gson();
        List<JsonObject> teamJsonList = new ArrayList<>();
        for (String entry : teamsList) {
            JsonObject json = gson.fromJson(entry, JsonObject.class);
            if (!json.get("displayNameKey").getAsString().equals("SPECTATOR")) {
                teamJsonList.add(json);
            }
        }
        int teamCount = teamJsonList.size();
        int teamSize = 0;
        for (JsonObject json : teamJsonList) {
            int maxPlayers = json.get("maxPlayers").getAsInt();
            teamSize = Math.max(teamSize, maxPlayers);
        }
        ctx.getSource().sendMessage(Component.text("Found " + teamJsonList.size() + " teams to import"));
        MapSize mapSize = new MapSize(teamCount, teamSize);
        for (JsonObject json : teamJsonList) {
            int weight = 0;
            w:
            while (true) {
                for (TeamType type : woolbattle.teamManager().teamTypes(mapSize)) {
                    if (type.getWeight() == weight) {
                        weight++;
                        continue w;
                    }
                }
                break;
            }
            String displayNameKey = json.get("displayNameKey").getAsString().toLowerCase(Locale.ROOT);
            boolean enabled = json.get("enabled").getAsBoolean();
            DyeColor woolcolor = DyeColor.getByWoolData(json.get("woolcolor").getAsByte());
            ChatColor namecolor = ChatColor.getByChar(json.get("namecolor").getAsString().charAt(0));

            TeamType teamType = woolbattle.teamManager().create(mapSize, displayNameKey, weight, woolcolor, namecolor, enabled);
            ctx.getSource().sendMessage(Component.text("Imported Team " + teamType.getDisplayNameKey() + " on MapSize " + mapSize));
        }
        return mapSize;
    }
}
