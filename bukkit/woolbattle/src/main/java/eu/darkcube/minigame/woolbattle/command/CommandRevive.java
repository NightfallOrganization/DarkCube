/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CommandRevive extends CommandExecutor {

	public CommandRevive() {
		super("woolbattle", "revive", "woolbattle.command.revive", new String[0], b -> b.then(
				Commands.argument("player", EntityArgument.player()).executes(context -> {
					Player p = EntityArgument.getPlayer(context, "player");
					User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
					Ingame ingame = WoolBattle.getInstance().getIngame();
					if (!ingame.lastTeam.containsKey(user)) {
						context.getSource().sendMessage(
								Component.text("Konnte team für spieler nicht finden!"));
						return 0;
					}
					Team team = ingame.lastTeam.get(user);
					WoolBattle.getInstance().getTeamManager().setTeam(user, team);
					context.getSource().sendMessage(Component.text("Spieler wiederbelebt!"));
					return 0;
				})));
	}

}
