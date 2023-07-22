/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class SetLifesCommand extends PServerExecutor {

	public SetLifesCommand() {
		super("setlifes", new String[0], b -> b.then(
						Commands.argument("lifes", IntegerArgumentType.integer(0, 999))
								.requires(source -> WoolBattle.instance().getLobby().enabled())
								.executes(context -> {
									int lifes = IntegerArgumentType.getInteger(context, "lifes");
									WoolBattle.instance().baseLifes = lifes;
									context.getSource()
											.sendMessage(Message.WOOLBATTLE_SETLIFES_LIFES, lifes);
									return 0;
								}))
				.then(Commands.argument("team", TeamArgument.teamArgument(TeamType::isEnabled))
						.requires(source -> WoolBattle.instance().getIngame().enabled())
						.then(Commands.argument("lifes", IntegerArgumentType.integer(0, 999))
								.executes(context -> {
									TeamType type = TeamArgument.getTeam(context, "team");
									int lifes = IntegerArgumentType.getInteger(context, "lifes");
									Team team =
											WoolBattle.instance().getTeamManager().getTeam(type);
									team.setLifes(lifes);
									context.getSource()
											.sendMessage(Message.WOOLBATTLE_SETLIFES_TEAM_LIFES,
													Component.text(
																	team.getType().getDisplayNameKey())
															.style(team.getPrefixStyle()), lifes);
									return 0;
								}))));
	}

}
