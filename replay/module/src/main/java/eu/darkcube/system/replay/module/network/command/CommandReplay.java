/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.module.network.command;

import de.dytanic.cloudnet.command.sub.SubCommandArgumentTypes;
import de.dytanic.cloudnet.command.sub.SubCommandBuilder;
import de.dytanic.cloudnet.command.sub.SubCommandHandler;

public class CommandReplay extends SubCommandHandler {
	public CommandReplay() {
		super(new String[] {"replay"}, "darkcube.replay.module", SubCommandBuilder.create()
				.generateCommand(
						(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
							
						}, SubCommandArgumentTypes.exactStringIgnoreCase("test")).getSubCommands());
	}
}
