/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class ReviveCommand extends PServerExecutor {
	public ReviveCommand() {
		super("revive", new String[0], b -> b.then(
				Commands.argument("player", EntityArgument.player()).executes(context -> {
					User user = WoolBattle.getInstance().getUserWrapper()
							.getUser(EntityArgument.getPlayer(context, "player").getUniqueId());
					if (WoolBattle.getInstance().getIngame().revive(user)) {
						context.getSource().sendMessage(Message.WOOLBATTLE_REVIVED_PLAYER,
								user.getTeamPlayerName());
					} else {
						context.getSource().sendMessage(Message.WOOLBATTLE_REVIVE_NO_TEAM_FOUND,
								user.getTeamPlayerName());
					}
					return 0;
				})));
	}

}
