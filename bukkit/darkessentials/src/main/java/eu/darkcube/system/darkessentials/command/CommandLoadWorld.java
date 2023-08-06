/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.io.File;
import java.util.List;

public class CommandLoadWorld extends EssentialsCommand {
	public CommandLoadWorld() {
		super("loadworld",
				b -> b.then(Commands.argument("world", StringArgumentType.string()).executes(ctx -> {
					String worldName = StringArgumentType.getString(ctx, "world");
					if (!new File(DarkEssentials.getInstance().getServer().getWorldContainer(),
							worldName).exists() && !new File(
							DarkEssentials.getInstance().getServer().getWorldContainer()
									.getParent(), worldName).exists()) {
						ctx.getSource().sendMessage(Component.text("Diese Welt existiert nicht"));
						return 0;
					}
					YamlConfiguration cfg = DarkEssentials.getInstance().getConfig("worlds");
					List<String> worlds = cfg.getStringList("worlds");
					if (worlds.contains(worldName)) {
						ctx.getSource()
								.sendMessage(Component.text("Diese Welt existiert " + "bereits"));
						return 0;
					}
					worlds.add(worldName);
					cfg.set("worlds", worlds);
					DarkEssentials.getInstance().saveConfig(cfg);

					WorldCreator wc = new WorldCreator(worldName);
					World world = wc.createWorld();
					if (ctx.getSource().getEntity() instanceof Player) {
						Player player = (Player) ctx.getSource().getEntity();
						player.teleport(world.getSpawnLocation(), TeleportCause.PLUGIN);
					}

					ctx.getSource().sendMessage(Component.text("Welt geladen"));
					return 0;
				})));
	}
}
