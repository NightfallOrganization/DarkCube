package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import java.util.Collection;

import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.Main;
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

public class SetTeamCommand extends PServerExecutor {

	public SetTeamCommand() {
		super("setteam", new String[0],
				b -> b.then(Commands.argument("team", TeamArgument.teamArgument()).executes(context -> {
					SetTeamCommand.setTeam(context.getSource(), Arrays.asList(context.getSource().asPlayer()),
							TeamArgument.getTeam(context, "team"));
					return 0;
				}).then(Commands.argument("players", EntityArgument.players()).executes(context -> {
					SetTeamCommand.setTeam(context.getSource(), EntityArgument.getPlayers(context, "players"),
							TeamArgument.getTeam(context, "team"));
					return 0;
				}))));
	}

	private static void setTeam(CommandSource source, Collection<Player> players, TeamType teamtype) {
		Team team = Main.getInstance().getTeamManager().getTeam(teamtype);
		for (Player player : players) {
			User user = Main.getInstance().getUserWrapper().getUser(player.getUniqueId());
			Main.getInstance().getTeamManager().setTeam(user, team);
		}
		if (players.size() == 1) {
			source.sendFeedback(Message.WOOLBATTLE_SETTEAM_TEAM_SINGLE.getMessage(source,
					players.stream().findAny().get().getName(), teamtype.getDisplayNameKey()), true);
		} else {
			source.sendFeedback(Message.WOOLBATTLE_SETTEAM_TEAM_MULTIPLE.getMessage(source, players.size(),
					teamtype.getDisplayNameKey()), true);
//			for (Player player : players) {
//				User user = Main.getInstance().getUserWrapper().getUser(player.getUniqueId());
//			}
		}

	}
}
