package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class SetLifesCommand extends PServerExecutor {

	public SetLifesCommand() {
		super("setlifes", new String[0],
						b -> b.then(Commands.argument("lifes", IntegerArgumentType.integer(0, 999)).requires(source -> {
							return Main.getInstance().getLobby().isEnabled();
						}).executes(context -> {
							int lifes = IntegerArgumentType.getInteger(context, "lifes");
							Main.getInstance().baseLifes = lifes;
							context.getSource().sendFeedback(Message.WOOLBATTLE_SETLIFES_LIFES.getMessage(context.getSource(), lifes), true);
							return 0;
						})).then(Commands.argument("team", TeamArgument.teamArgument(t -> t.isEnabled())).then(Commands.argument("lifes", IntegerArgumentType.integer(0, 999)).requires(source -> Main.getInstance().getIngame().isEnabled()).executes(context -> {
							TeamType type = TeamArgument.getTeam(context, "team");
							int lifes = IntegerArgumentType.getInteger(context, "lifes");
							Team team = Main.getInstance().getTeamManager().getTeam(type);
							team.setLifes(lifes);
							context.getSource().sendFeedback(Message.WOOLBATTLE_SETLIFES_TEAM_LIFES.getMessage(context.getSource(), team.getPrefix()
											+ team.getType().getDisplayNameKey(), lifes), true);
							return 0;
						}))));
	}

}