/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SetTeamCommand extends PServerExecutor {

	public SetTeamCommand() {
		super("setteam", new String[0], b -> b.then(
				Commands.argument("team", TeamArgument.teamArgument()).executes(context -> {
					SetTeamCommand.setTeam(context.getSource(),
							Arrays.asList(context.getSource().asPlayer()),
							TeamArgument.getTeam(context, "team"));
					return 0;
				}).then(Commands.argument("players", EntityArgument.players()).executes(context -> {
					SetTeamCommand.setTeam(context.getSource(),
							EntityArgument.getPlayers(context, "players"),
							TeamArgument.getTeam(context, "team"));
					return 0;
				}))));
	}

	private static void setTeam(CommandSource source, Collection<Player> players,
			TeamType teamtype) {
		Team team = WoolBattle.getInstance().getTeamManager().getTeam(teamtype);
		for (Player player : players) {
			User user = WoolBattle.getInstance().getUserWrapper().getUser(player.getUniqueId());
			WoolBattle.getInstance().getTeamManager().setTeam(user, team);
		}
		if (players.size() == 1) {
			source.sendMessage(Message.WOOLBATTLE_SETTEAM_TEAM_SINGLE,
					players.stream().findAny().get().getName(), teamtype.getDisplayNameKey());
		} else {
			source.sendMessage(Message.WOOLBATTLE_SETTEAM_TEAM_MULTIPLE, players.size(),
					teamtype.getDisplayNameKey());
			//			for (Player player : players) {
			//				User user = Main.getInstance().getUserWrapper().getUser(player.getUniqueId());
			//			}
		}

	}
}
