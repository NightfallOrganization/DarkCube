/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.bukkit.command;

import eu.darkcube.system.cloudban.bukkit.Main;
import eu.darkcube.system.cloudban.command.Command;
import eu.darkcube.system.cloudban.command.wrapper.CommandWrapper;
import eu.darkcube.system.util.ReflectionUtils;
import eu.darkcube.system.util.ReflectionUtils.PackageType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class BukkitCommandWrapper implements TabExecutor {

	private static final Class<?> VANILLA_COMMAND_WRAPPER = ReflectionUtils.getClass("VanillaCommandWrapper",
			PackageType.CRAFTBUKKIT_COMMAND);

	public static void create() {
		CommandWrapper.load();
		for (Command cmd : Command.COMMANDS) {
			new BukkitCommandWrapper(cmd);
		}
	}

	private Command handle;

	private BukkitCommandWrapper(Command handle) {
		this.registerCommands(JavaPlugin.getPlugin(Main.class), handle);
		this.handle = handle;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label,
			String[] args) {
		return this.handle.onTabComplete(new CommandSenderBukkit(sender), cmd.getName(), args, label);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		this.handle.execute(new CommandSenderBukkit(sender), cmd.getName(), args, label);
		return true;
	}

	@SuppressWarnings("unchecked")
	private synchronized org.bukkit.command.Command registerCommands(JavaPlugin plugin, Command owner) {
		try {
			Set<Command> commandSet = new HashSet<>();
			commandSet.add(owner);
			for (Command command : commandSet) {
				Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class,
						Plugin.class);
				constructor.setAccessible(true);
				PluginCommand plugincommand = constructor.newInstance(command.getNames()[0], plugin);
				List<String> aliases = new ArrayList<>(Arrays.asList(owner.getNames()));
				aliases.remove(command.getNames()[0]);
				plugincommand.setAliases(aliases);
				plugincommand.setUsage(command.getUsage());
				plugincommand.setPermission(command.getPermission());
				Object server = Bukkit.getServer();
				Method methodGETCOMMANDMAP = server.getClass().getMethod("getCommandMap");
				CommandMap commandMap = (CommandMap) methodGETCOMMANDMAP.invoke(server);
				commandMap.register("eu/darkcube/system", plugincommand);
				Field f = commandMap.getClass().getDeclaredField("knownCommands");
				f.setAccessible(true);
				Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>) f
						.get(commandMap);
				if (BukkitCommandWrapper.VANILLA_COMMAND_WRAPPER
						.isInstance(knownCommands.get(owner.getNames()[0].toLowerCase()))) {
					knownCommands.put(owner.getNames()[0].toLowerCase(), plugincommand);
				}
				for (String alias : aliases) {
					if (BukkitCommandWrapper.VANILLA_COMMAND_WRAPPER
							.isInstance(knownCommands.get(alias.toLowerCase()))) {
						knownCommands.put(alias.toLowerCase(), plugincommand);
					}
				}
				plugincommand.setExecutor(this);
				plugincommand.setTabCompleter(this);
				return plugincommand;
			}
		} catch (SecurityException ex) {
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
