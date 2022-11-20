package eu.darkcube.minigame.woolbattle.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandSetLifes extends CommandExecutor {

	public CommandSetLifes() {
		super("woolbattle", "setlifes", "woolbattle.command.setlifes",
						new String[0],
						b -> b.then(Commands.argument("lifes", IntegerArgumentType.integer(0, 99)).executes(context -> {
							WoolBattle.getInstance().baseLifes = IntegerArgumentType.getInteger(context, "lifes");
							context.getSource().sendFeedback(CustomComponentBuilder.cast(TextComponent.fromLegacyText(Message.CHANGED_LIFES.getServerMessage(Integer.toString(WoolBattle.getInstance().baseLifes)))), true);
							return 0;
						})).then(Commands.argument("team", TeamArgument.teamArgument(t -> t.isEnabled())).then(Commands.argument("lifes", IntegerArgumentType.integer(0, 99)).executes(context -> {
							Team team = WoolBattle.getInstance().getTeamManager().getTeam(TeamArgument.getTeam(context, "team"));
							team.setLifes(IntegerArgumentType.getInteger(context, "lifes"));
							context.getSource().sendFeedback(CustomComponentBuilder.cast(TextComponent.fromLegacyText("Leben gesetzt.")), true);
							return 0;
						}))));
	}

}
