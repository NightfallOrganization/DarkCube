/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.skyland.worldGen.CustomChunkGenerator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.io.File;
import java.util.Locale;

public class SkylandGeneratorCommand extends CommandExecutor {
	public SkylandGeneratorCommand(Skyland skyland) {
		super("skyland", "skylandgenerator", new String[0], b -> b.then(Commands.literal("create")
				.then(Commands.argument("name", StringArgumentType.word()).executes(ctx -> {
					String name =
							StringArgumentType.getString(ctx, "name").toLowerCase(Locale.ROOT);
					for (World world : Bukkit.getWorlds()) {
						if (!world.getKey().namespace()
								.equals(skyland.getName().toLowerCase(Locale.ROOT)))
							continue;
						if (name.equals(world.getKey().value())) {
							ctx.getSource().sendMessage(
									Component.text("Diese Welt existiert bereits!")
											.color(NamedTextColor.RED));
							return 0;
						}
					}
					WorldCreator worldCreator = new WorldCreator(new NamespacedKey(skyland, name));
					worldCreator.generator(new CustomChunkGenerator());
					worldCreator.seed(1234);
					World world = worldCreator.createWorld();
					if (ctx.getSource().getEntity() instanceof Player) {
						Player player = ctx.getSource().asPlayer();
						player.teleportAsync(new Location(world, 0, 100, 0), TeleportCause.PLUGIN);
					}
					return 0;
				}))).then(Commands.literal("delete")
				.then(Commands.argument("name", StringArgumentType.word()).executes(ctx -> {
					String name =
							StringArgumentType.getString(ctx, "name").toLowerCase(Locale.ROOT);
					NamespacedKey key = new NamespacedKey(skyland, name);
					World world = Bukkit.getWorld(key);
					if (world == null) {
						ctx.getSource().sendMessage(Component.text("Diese Welt existert nicht!")
								.color(NamedTextColor.RED));
						return 0;
					}
					File folder = world.getWorldFolder();
					if (Bukkit.unloadWorld(world, false)) {
						deleteFiles(folder);
						ctx.getSource().sendMessage(
								Component.text("Welt wurde gelöscht!").color(NamedTextColor.GREEN));
					} else {
						ctx.getSource().sendMessage(
								Component.text("Welt konnte nicht gelöscht werden!")
										.color(NamedTextColor.RED));
					}
					return 0;
				}))));
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
