/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.troll;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class CommandToggle extends WBCommandExecutor {
	public CommandToggle() {
		super("toggle", b -> b.executes(
						ctx -> toggle(ctx, Collections.singleton(ctx.getSource().asPlayer())))
				.then(Commands.argument("players", EntityArgument.players())
						.executes(ctx -> toggle(ctx, EntityArgument.getPlayers(ctx, "players")))));
	}

	private static int toggle(CommandContext<CommandSource> ctx, Collection<Player> players) {
		for (Player p : players) {
			WBUser user = WBUser.getUser(p);
			user.setTrollMode(!user.isTrollMode());
			StatsLink.enabled = false;
		}
		return 0;
	}
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		Set<Player> players = new HashSet<>();
	//		if (args.length >= 1) {
	//			for (String pn : args) {
	//				Player p = Bukkit.getPlayer(pn);
	//				if (p != null) {
	//					players.add(p);
	//				}
	//			}
	//		} else {
	//			if (sender instanceof Player) {
	//				players.add((Player) sender);
	//			}
	//		}
	//		for (Player p : players) {
	//			WBUser user = WBUser.getUser(p);
	//			user.setTrollMode(!user.isTrollMode());
	//			StatsLink.enabled = false;
	//		}
	//		return true;
	//	}
}
