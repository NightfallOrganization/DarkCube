package eu.darkcube.system.miners.command;

import org.bukkit.entity.Player;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.BooleanArgument;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;

public class CommandTeam extends CommandExecutor {

	public CommandTeam() {
		super("miners", "team", new String[0],
				b -> b.then(
						Commands.argument("team", IntegerArgumentType.integer(0, Miners.getMinersConfig().TEAM_COUNT))
								.executes(context -> {
									return setTeam(context.getSource().asPlayer(),
											IntegerArgumentType.getInteger(context, "team"), false);
								}).then(Commands.argument("allowInFullTeam", BooleanArgument.booleanArgument())
										.executes(context -> {
											return setTeam(context.getSource().asPlayer(),
													IntegerArgumentType.getInteger(context, "team"),
													BooleanArgument.getBoolean(context, "allowInFullTeam"));
										}))));
	}

	private static int setTeam(Player player, int team, boolean allowInFullTeam) {
		if (Miners.getGamephase() != 0) {
			Miners.sendTranslatedMessage(player, Message.FAIL_GAME_RUNNING);
			return 0;
		}
		if (!Miners.getTeamManager().setPlayerTeam(player, team, allowInFullTeam))
			Miners.sendTranslatedMessage(player, Message.FAIL_TEAM_FULL);
		return 0;
	}

}
