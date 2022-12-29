/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;
import org.bukkit.Bukkit;

public class StopCommand extends PServerExecutor {

	public StopCommand() {
		super("stop", new String[] {"shutdown"}, b -> b.executes(source -> {
			source.getSource().sendMessage(Message.SHUTTING_DOWN_SERVER);
			Bukkit.shutdown();
			return 0;
		}));
	}
}
