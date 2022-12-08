/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.module.command;

import java.util.Collection;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.ICommandSender;
import de.dytanic.cloudnet.command.ITabCompleter;
import de.dytanic.cloudnet.common.Properties;
import eu.darkcube.system.cloudban.command.wrapper.CommandWrapper;
import eu.darkcube.system.cloudban.module.Module;

public class CloudCommandWrapper extends Command implements ITabCompleter {

	public static void create() {
		CommandWrapper.load();
		for (eu.darkcube.system.cloudban.command.Command cmd : eu.darkcube.system.cloudban.command.Command.COMMANDS) {
			new CloudCommandWrapper(cmd);
		}
	}

	private eu.darkcube.system.cloudban.command.Command cmd;

	private CloudCommandWrapper(eu.darkcube.system.cloudban.command.Command cmd) {
		super(cmd.getNames());
		this.cmd = cmd;
		this.description = cmd.getDescription();
		this.permission = cmd.getPermission();
		this.prefix = cmd.getPrefix();
		this.usage = cmd.getUsage();
		Module.getCloudNet().getCommandMap().registerCommand(this);
	}

	@Override
	public void execute(ICommandSender sender, String command, String[] args, String commandLine,
			Properties properties) {
		cmd.execute(new CommandSenderCloud(sender), command, args, commandLine);
	}

	@Override
	public Collection<String> complete(String commandLine, String[] args, Properties properties) {
		return cmd.onTabComplete(new CommandSenderCloud(Module.getCloudNet().getConsoleCommandSender()), this.names[0],
				args, commandLine);
	}
}
