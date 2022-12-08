/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class GameModeCommand extends PServerExecutor {

	@SuppressWarnings("deprecation")
	public GameModeCommand() {
		super("gamemode", new String[] {
						"gm"
		}, b -> b.then(Commands.argument("mode", EnumArgument.enumArgument(GameMode.values(), gm -> new String[] {
						gm.name().toLowerCase(), Integer.toString(gm.getValue())
		})).executes(context -> {
			return gamemode(context, Collections.singleton(context.getSource().asPlayer()), EnumArgument.getEnumArgument(context, "mode", GameMode.class));
		}).then(Commands.argument("targets", EntityArgument.players()).executes(context -> {
			return gamemode(context, EntityArgument.getPlayers(context, "targets"), EnumArgument.getEnumArgument(context, "mode", GameMode.class));
		}))));
	}

	private static int gamemode(CommandContext<CommandSource> context,
					Collection<Player> players, GameMode gamemode) {
		players.forEach(p -> {
			p.setGameMode(gamemode);
		});
		if (players.size() == 1) {
			context.getSource().sendFeedback(Message.CHANGED_GAMEMODE_SINGLE.getMessage(context.getSource(), players.stream().findAny().get().getName(), gamemode.name().toLowerCase()), true);
		} else {
			context.getSource().sendFeedback(Message.CHANGED_GAMEMODE_MULTIPLE.getMessage(context.getSource(), players.size(), gamemode.name().toLowerCase()), true);
		}
		return 0;
	}
}
