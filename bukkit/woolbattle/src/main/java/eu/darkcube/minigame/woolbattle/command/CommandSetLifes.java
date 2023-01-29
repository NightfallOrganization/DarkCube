/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CommandSetLifes extends CommandExecutor {

	public CommandSetLifes() {
		super("woolbattle", "setlifes", "woolbattle.command.setlifes", new String[0], b -> b.then(
				Commands.argument("lifes", IntegerArgumentType.integer(0, 99)).executes(context -> {
					WoolBattle.getInstance().baseLifes =
							IntegerArgumentType.getInteger(context, "lifes");
					context.getSource().sendMessage(LegacyComponentSerializer.legacySection()
							.deserialize(Message.CHANGED_LIFES.getServerMessage(
									Integer.toString(WoolBattle.getInstance().baseLifes))));
					return 0;
				})).then(Commands.argument("team", TeamArgument.teamArgument(t -> t.isEnabled()))
				.then(Commands.argument("lifes", IntegerArgumentType.integer(0, 99))
						.executes(context -> {
							Team team = WoolBattle.getInstance().getTeamManager()
									.getTeam(TeamArgument.getTeam(context, "team"));
							team.setLifes(IntegerArgumentType.getInteger(context, "lifes"));
							context.getSource().sendMessage(Component.text("Leben gesetzt!"));
							return 0;
						}))));
	}

}
