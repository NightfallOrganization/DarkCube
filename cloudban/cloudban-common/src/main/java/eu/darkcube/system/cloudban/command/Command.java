/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Command {

	public static final Set<Command> COMMANDS = new HashSet<>();

	private String[] names;
	private String usage;
	private String permission;
	private String prefix;
	private String description;

	public Command(String[] names, String usage, String permission, String prefix, String description) {
		this(names, usage, permission, prefix, description, true);
	}

	public Command(String[] names, String usage, String permission, String prefix, String description, boolean add) {
		this.names = names;
		this.usage = usage;
		this.permission = permission;
		this.prefix = prefix;
		this.description = description;
		if (add)
			COMMANDS.add(this);
	}

	public String[] getNames() {
		return names;
	}

	public String getDescription() {
		return description;
	}

	public String getPermission() {
		return permission;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getUsage() {
		return usage;
	}
	
	protected void sendUsage(CommandSender sender) {
		sender.sendMessage("&c" + getUsage());
	}

	public abstract List<String> onTabComplete(CommandSender sender, String command, String[] args,
			String commandLine);

	public abstract void execute(CommandSender sender, String command, String[] args, String commandLine);

}
