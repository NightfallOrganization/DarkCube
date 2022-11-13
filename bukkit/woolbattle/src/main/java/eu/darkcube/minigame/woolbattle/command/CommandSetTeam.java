package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandSetTeam extends CommandExecutor {

	public CommandSetTeam() {
		super("woolbattle", "setteam", "woolbattle.command.setteam",
						new String[0], b -> {
							b.then(Commands.argument("player", EntityArgument.player()).then(Commands.argument("team", TeamArgument.teamArgument()).executes(context -> {
								Player player = EntityArgument.getPlayer(context, "player");
								User user = Main.getInstance().getUserWrapper().getUser(player.getUniqueId());
								TeamType type = TeamArgument.getTeam(context, "team");
								Team team = Main.getInstance().getTeamManager().getTeam(type);
								user.setTeam(team);
								context.getSource().sendFeedback(CustomComponentBuilder.cast(TextComponent.fromLegacyText("Team gesetzt.")), true);
								return 0;
							})));
						});
	}

}
