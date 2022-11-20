package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandRevive extends CommandExecutor {

	public CommandRevive() {
		super("woolbattle", "revive", "woolbattle.command.revive",
						new String[0],
						b -> b.then(Commands.argument("player", EntityArgument.player()).executes(context -> {
							Player p = EntityArgument.getPlayer(context, "player");
							User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
							Ingame ingame = WoolBattle.getInstance().getIngame();
							if (!ingame.lastTeam.containsKey(user)) {
								context.getSource().sendFeedback(CustomComponentBuilder.cast(TextComponent.fromLegacyText("Konnte team für spieler nicht finden!")), true);
								return 0;
							}
							Team team = ingame.lastTeam.get(user);
							WoolBattle.getInstance().getTeamManager().setTeam(user, team);
							context.getSource().sendFeedback(CustomComponentBuilder.cast(TextComponent.fromLegacyText("Spieler wiederbelebt!")), true);
							return 0;
						})));
	}

}
