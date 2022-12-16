/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.brigadier.suggestion.Suggestion;
import com.viaversion.viaversion.api.Via;

import net.md_5.bungee.api.chat.TextComponent;

public class CommandAPI {

	private final JavaPlugin plugin;

	private Commands commands;

	private final Executor executor = new Executor();

	private static CommandAPI instance;

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

	public void dispatchCommand(String commandLine, CommandSender sender) {
		this.commands.executeCommand(sender, commandLine);
	}

	private String sjoin(String label, String[] args) {
		return args.length == 0 ? label : (label + " " + String.join(" ", args));
	}

	@SuppressWarnings("unchecked")
	private void pluginRegisterCommand(final CommandExecutor command) {
		try {
			final Constructor<PluginCommand> constructor =
					PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			constructor.setAccessible(true);
			final String name = command.getName().toLowerCase();
			if (name.contains(" ")) {
				throw new IllegalArgumentException(
						"Can't register command with whitespace in name!");
			}
			final String[] aliases = command.getAliases();
			final PluginCommand plugincommand = constructor.newInstance(name, this.plugin);
			plugincommand.setAliases(Arrays.asList(aliases));
			plugincommand.setUsage("/" + name);
			plugincommand.setPermission(command.getPermission());
			plugincommand.setExecutor(this.executor);
			plugincommand.setTabCompleter(this.executor);
			Field ftimings = plugincommand.getClass().getField("timings");
			Method timingsOf = Class.forName("co.aikar.timings.Timings")
					.getMethod("of", Plugin.class, String.class);
			ftimings.set(plugincommand, timingsOf.invoke(null, this.plugin,
					this.plugin.getName() + ":" + command.getName()));
			final Object server = Bukkit.getServer();
			final Method methodGETCOMMANDMAP = server.getClass().getMethod("getCommandMap");
			final Object commandMap = methodGETCOMMANDMAP.invoke(server);
			final Field f = commandMap.getClass().getDeclaredField("knownCommands");
			f.setAccessible(true);
			final Map<String, Command> knownCommands = (Map<String, Command>) f.get(commandMap);
			final String prefix = command.getPrefix().toLowerCase();
			if (prefix != null) {
				if (prefix.contains(" ")) {
					throw new IllegalArgumentException(
							"Can't register command with whitespace in prefix!");
				}
				for (String n : command.getNames()) {
					final String registerName = prefix + ":" + n;
					this.checkAmbiguities(registerName, knownCommands, plugincommand);
				}
			}
			for (String n : command.getNames()) {
				this.checkAmbiguities(n, knownCommands, plugincommand);
			}
			for (final String n : command.getNames()) {
				knownCommands.put(n.toLowerCase(), plugincommand);
			}
			if (command.getPermission() != null) {
				if (Bukkit.getPluginManager().getPermission(command.getPermission()) == null) {
					Bukkit.getPluginManager()
							.addPermission(new Permission(command.getPermission()));
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
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

	private void checkAmbiguities(String registerName, Map<String, Command> knownCommands,
			PluginCommand command) {
		if (knownCommands.containsKey(registerName)) {
			Command cmd = knownCommands.get(registerName);
			if (cmd instanceof PluginCommand) {
				PluginCommand pcmd = (PluginCommand) cmd;
				if (pcmd.getExecutor() == this.executor
						&& pcmd.getTabCompleter() == this.executor) {
					return;
				}
				Bukkit.getConsoleSender().sendMessage(
						"§6Overriding command " + registerName + " from Plugin " + pcmd.getPlugin()
								.getName() + "!");
			} else {
				Bukkit.getConsoleSender().sendMessage(
						"§6Overriding command " + registerName + " from type " + cmd.getClass()
								.getSimpleName() + "!");
			}
		}
		knownCommands.put(registerName, command);
	}

	private class Executor implements TabExecutor {

		@Override
		public List<String> onTabComplete(CommandSender sender, Command command, String label,
				String[] args) {
			String commandLine = CommandAPI.this.sjoin(label, args);
			String commandLine2 =
					CommandAPI.this.sjoin(label, Arrays.copyOfRange(args, 0, args.length - 1))
							+ " ";
			List<Suggestion> completions =
					CommandAPI.this.commands.getTabCompletions(sender, commandLine);
			ICommandExecutor executor = new BukkitCommandExecutor(sender);
			if (!completions.isEmpty()) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					int version = Via.getAPI().getPlayerVersion(p.getUniqueId());
					if (version < 393) { // 393 is the version for 1.13
						// https://minecraft.fandom.com/wiki/Protocol_version
						executor.sendMessage(new TextComponent(" "));
						executor.sendCompletions(commandLine, completions);
					}
				}
			}
			List<String> r = new ArrayList<>();
			for (Suggestion completion : completions) {
				r.add(completion.apply(commandLine).substring(commandLine2.length()));
			}
			return r;
		}

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label,
				String[] args) {
			String commandLine = CommandAPI.this.sjoin(label, args);
			CommandAPI.this.dispatchCommand(commandLine, sender);
			return true;
		}

	}

}
