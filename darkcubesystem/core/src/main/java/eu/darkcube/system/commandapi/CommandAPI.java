/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi;

import eu.darkcube.system.version.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandAPI {

	protected Command main_command;

	protected String prefix = "§7§l[§5Dark§dCube§7§l] ";

	protected JavaPlugin plugin;

	protected static final CommandPosition standartPos = new CommandPosition(-1);

	protected Set<String> knownPermissions = new HashSet<>();

	public Set<String> getKnownPermissions() {
		return this.knownPermissions;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	private CommandAPI(JavaPlugin plugin, Command owner) {
		this.main_command = owner;
		this.plugin = plugin;
	}

	/**
	 * @param plugin - The Plugin that executes the Command
	 * @param owner - MUST be a Command with the name of the Bukkit-Command
	 * @return CommandAPI - The CommandAPI instance
	 */
	public static synchronized CommandAPI enable(JavaPlugin plugin, Command owner) {
		owner.setPositions(CommandAPI.standartPos);

		CommandAPI api = new CommandAPI(plugin, owner);
		for (Command apicmd : api.getMainCommand().getAllChilds()) {
			apicmd.instance = api;
		}
		api.getMainCommand().instance = api;
		api.getMainCommand().loadSimpleLongUsage();
		PluginCommand cmd = CommandAPI.registerCommands(plugin, owner);

		CommandHandler handler = new CommandHandler(api);
		cmd.setAliases(Arrays.asList(owner.getAliases()));
		cmd.setExecutor(handler);
		cmd.setTabCompleter(handler);
		api.loadPermissions(api.main_command);
		for (String perm : api.getKnownPermissions()) {
			try {
				Bukkit.getPluginManager().addPermission(new Permission(perm));
			} catch (Exception ignored) {
			}
		}
		return api;
	}

	private static synchronized PluginCommand registerCommands(JavaPlugin plugin, Command owner) {
		return VersionSupport.getVersion().commandApi().registerLegacy(plugin, owner);
	}

	private void loadPermissions(Command cmd) {
		Set<Command> childs = this.checkPermissionAndAdd(cmd);
		for (Command child : childs)
			this.loadPermissions(child);
	}

	private Set<Command> checkPermissionAndAdd(Command cmd) {
		this.knownPermissions.add(cmd.getPermission());
		cmd.instance = this;
		return cmd.getChilds();
	}

	public Command getMainCommand() {
		return this.main_command;
	}

}