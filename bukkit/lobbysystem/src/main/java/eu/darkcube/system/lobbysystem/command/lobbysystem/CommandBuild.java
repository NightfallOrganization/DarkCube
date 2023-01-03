/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class CommandBuild extends LobbyCommandExecutor {

	public CommandBuild() {
		super("build", b -> b.then(Commands.argument("players", EntityArgument.players())
						.executes(ctx -> toggle(ctx, EntityArgument.getPlayers(ctx, "players"))))
				.executes(ctx -> toggle(ctx, Collections.singleton(ctx.getSource().asPlayer()))));
	}

	private static int toggle(CommandContext<CommandSource> ctx, Collection<Player> players)
			throws CommandSyntaxException {
		for (Player t : players) {
			LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(t));
			if (user.isBuildMode()) {
				Lobby.getInstance().setupPlayer(user);
				user.setBuildMode(false);
				Lobby.getInstance().sendMessage("§cNun nicht mehr im §eBau-Modus§c!", t);
				if (!ctx.getSource().assertIsEntity().equals(t)) {
					ctx.getSource().sendMessage(
							Component.text(t.getName()).color(NamedTextColor.GOLD)
									.append(Component.text(" ist nun nicht mehr im ")
											.color(NamedTextColor.RED))
									.append(Component.text("Bau-Modus")
											.color(NamedTextColor.YELLOW))
									.append(Component.text("!").color(NamedTextColor.RED)));
					// Lobby.getInstance().sendMessage(
					// "§6" + name + "§c ist nun nicht mehr im §eBau-Modus§c!", sender);
				}
			} else {
				Lobby.getInstance().savePlayer(user);
				user.setBuildMode(true);
				t.setGameMode(GameMode.CREATIVE);
				Lobby.getInstance().sendMessage("§aNun im §eBau-Modus§a!", t);
				if (!ctx.getSource().assertIsEntity().equals(t)) {
					ctx.getSource().sendMessage(
							Component.text(t.getName()).color(NamedTextColor.GOLD)
									.append(Component.text(" ist nun im ")
											.color(NamedTextColor.GREEN))
									.append(Component.text("Bau-Modus")
											.color(NamedTextColor.YELLOW))
									.append(Component.text("!").color(NamedTextColor.GREEN)));
				}
			}
		}
		return 0;
	}

}
