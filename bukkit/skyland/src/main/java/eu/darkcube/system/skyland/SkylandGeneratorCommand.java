/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SkylandGeneratorCommand extends Command {
    public SkylandGeneratorCommand(Skyland skyland) {
        super("skyland", "skylandgenerator", new String[0], b -> b
                .then(Commands.literal("create").then(Commands.argument("name", StringArgumentType.word()).executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "name").toLowerCase(Locale.ROOT);
                    for (World world : Bukkit.getWorlds()) {
                        if (!world.getKey().namespace().equals(skyland.getName().toLowerCase(Locale.ROOT))) continue;
                        if (name.equals(world.getKey().value())) {
                            ctx.getSource().sendMessage(Component.text("Diese Welt existiert bereits!").color(NamedTextColor.RED));
                            return 0;
                        }
                    }
                    ctx.getSource().sendMessage(Component.text("Welt wird erstellt...").color(NamedTextColor.GREEN));
                    WorldCreator worldCreator = new WorldCreator(new NamespacedKey(skyland, name));
                    worldCreator.generator(skyland.getDefaultWorldGenerator(worldCreator.name(), null));
                    worldCreator.seed(1234);
                    World world = worldCreator.createWorld();
                    if (ctx.getSource().getEntity() instanceof Player) {
                        Player player = ctx.getSource().asPlayer();
                        ctx.getSource().sendMessage(Component.text("Teleportiere...").color(NamedTextColor.GREEN));
                        player.teleportAsync(world.getSpawnLocation(), TeleportCause.PLUGIN);
                    }
                    return 0;
                })))
                .then(Commands.literal("delete").then(Commands.argument("name", StringArgumentType.word()).executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "name").toLowerCase(Locale.ROOT);
                    NamespacedKey key = new NamespacedKey(skyland, name);
                    World world = Bukkit.getWorld(key);
                    if (world == null) {
                        ctx.getSource().sendMessage(Component.text("Diese Welt existert nicht!").color(NamedTextColor.RED));
                        return 0;
                    }
                    deleteWorld(world, ctx.getSource());
                    return 0;
                }))));
    }

    public static void deleteCustomWorlds(Skyland skyland) {
        for (World world : new ArrayList<>(Bukkit.getWorlds())) {
            // TODO this will also delete future custom worlds created by other means. Everything
            //  that starts with "skyland:" will be deleted
            if (world.getKey().namespace().equals(skyland.getName().toLowerCase(Locale.ROOT))) {
                deleteWorld(world, null);
            }
        }
    }

    public static void deleteWorld(World world, Audience target) {
        World w = Bukkit.getWorlds().stream().filter(w2 -> w2 != world).findFirst().orElseThrow();
        File folder = world.getWorldFolder();
        for (Player player : world.getPlayers()) {
            player.teleport(w.getSpawnLocation(), TeleportCause.PLUGIN);
        }
        if (Bukkit.unloadWorld(world, false)) {
            deleteFiles(folder);
            if (target != null) target.sendMessage(Component.text("Welt wurde gelöscht!").color(NamedTextColor.GREEN));
        } else if (target != null) {
            target.sendMessage(Component.text("Welt konnte nicht gelöscht werden!").color(NamedTextColor.RED));
        }
    }

    private static boolean deleteFiles(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFiles(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }
}
