/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.util.StatsLink;

public class CommandIsStats extends WBCommand {
	public CommandIsStats() {
		super("isStats", b -> b.executes(ctx -> {
			if (StatsLink.isStats()) {
				ctx.getSource().sendMessage(Message.STATS_ARE_ENABLED);
			} else {
				ctx.getSource().sendMessage(Message.STATS_ARE_DISABLED);
			}
			return 0;
		}));
	}
}
