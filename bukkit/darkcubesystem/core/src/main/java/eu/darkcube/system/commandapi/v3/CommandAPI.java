/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.version.VersionSupport;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandAPI {

	private static CommandAPI instance;
	private final JavaPlugin plugin;
	private Commands commands;

	private CommandAPI(JavaPlugin plugin) {
		if (CommandAPI.instance != null) {
			throw new IllegalAccessError("You may not initialize the CommandAPI twice!");
		}
		this.plugin = plugin;
		this.commands = new Commands();
		CommandAPI.instance = this;
	}

	public static CommandAPI getInstance() {
		return CommandAPI.instance;
	}

	public static CommandAPI init(JavaPlugin plugin) {
		return new CommandAPI(plugin);
	}

	public void register(CommandExecutor command) {
		commands.register(command);
		this.pluginRegisterCommand(command);
	}

	public void unregisterByPrefix(String prefix) {
		commands.unregisterByPrefix(prefix);
	}

	public void unregisterPrefixlessByPrefix(String prefix) {
		commands.unregisterPrefixlessByPrefix(prefix);
	}

	private void pluginRegisterCommand(final CommandExecutor command) {
		VersionSupport.getVersion().commandApi().register(command);
	}

	public void unregisterAll() {
		this.commands = new Commands();
	}

	public Commands getCommands() {
		return this.commands;
	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

}
