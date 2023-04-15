/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class CommandsCommand extends PServerExecutor {

	public CommandsCommand() {
		super("commands", new String[] {"befehle", "help"}, b -> b.executes(context -> {
			CommandSource source = context.getSource();
			source.sendMessage(Message.COMMANDS_PREFIX, PServerExecutor.COMMAND_NAMES.size());
			for (String name : PServerExecutor.COMMAND_NAMES) {
				source.sendMessage(Message.COMMANDS_COMMANDINFO, name);
			}
			return 0;
		}));
	}

}
