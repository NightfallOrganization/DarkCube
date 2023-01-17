/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import co.aikar.timings.TimingsManager;
import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.v3.BukkitCommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.inventoryapi.item.ItemProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unused")
public class Version_1_8_8 implements Version {
	private CommandAPI commandAPI;
	private ItemProvider itemProvider;

	@Override
	public void init() {
		this.commandAPI = new CommandApi();
		this.itemProvider = new ItemProvider_1_8_8();
	}

	@Override
	public CommandAPI commandApi() {
		return commandAPI;
	}

	@Override
	public ItemProvider itemProvider() {
		return itemProvider;
	}

	private static class CommandApi implements CommandAPI {

		private final Executor executor = new Executor();

		@Override
		public String getSpigotUnknownCommandMessage() {
			return SpigotConfig.unknownCommandMessage;
		}

		@Override
		@SuppressWarnings("unchecked")
		public PluginCommand registerLegacy(Plugin plugin, Command command) {
			try {
				Constructor<PluginCommand> constructor =
						PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
				constructor.setAccessible(true);
				PluginCommand plugincommand = constructor.newInstance(command.getName(), plugin);
				plugincommand.setAliases(Arrays.asList(command.getAliases()));
				plugincommand.setUsage(command.getSimpleLongUsage());
				plugincommand.setPermission(command.getPermission());
				plugincommand.timings =
						TimingsManager.getCommandTiming(plugin.getName(), plugincommand);
				CraftServer server = (CraftServer) Bukkit.getServer();
				SimpleCommandMap commandMap = server.getCommandMap();
				Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
				f.setAccessible(true);
				Map<String, org.bukkit.command.Command> knownCommands =
						(Map<String, org.bukkit.command.Command>) f.get(commandMap);
				register(commandMap, knownCommands, plugin, plugincommand);
				return plugincommand;
			} catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException |
					InvocationTargetException | InstantiationException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void register(CommandExecutor command) {

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
				final PluginCommand plugincommand =
						constructor.newInstance(name, DarkCubeSystem.getInstance());
				plugincommand.setAliases(Arrays.asList(aliases));
				plugincommand.setUsage("/" + name);
				plugincommand.setPermission(command.getPermission());
				plugincommand.setExecutor(this.executor);
				plugincommand.setTabCompleter(this.executor);
				Field ftimings = plugincommand.getClass().getField("timings");
				Method timingsOf = Class.forName("co.aikar.timings.Timings")
						.getMethod("of", Plugin.class, String.class);
				ftimings.set(plugincommand, timingsOf.invoke(null, DarkCubeSystem.getInstance(),
						DarkCubeSystem.getInstance().getName() + ":" + command.getName()));
				SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
				final Field f = commandMap.getClass().getDeclaredField("knownCommands");
				f.setAccessible(true);
				final Map<String, org.bukkit.command.Command> knownCommands =
						(Map<String, org.bukkit.command.Command>) f.get(commandMap);
				final String prefix = command.getPrefix().toLowerCase();
				if (prefix.contains(" ")) {
					throw new IllegalArgumentException(
							"Can't register command with whitespace in prefix!");
				}
				for (String n : command.getNames()) {
					final String registerName = prefix + ":" + n;
					this.checkAmbiguities(registerName, knownCommands, plugincommand);
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
				throw new RuntimeException(ex);
			}
		}

		@Override
		public double[] getEntityBB(Entity entity) {
			AxisAlignedBB bb = ((CraftEntity) entity).getHandle().getBoundingBox();
			return new double[] {bb.a, bb.b, bb.c, bb.d, bb.e, bb.f};
		}

		private void register(CommandMap commandMap, Map<String, org.bukkit.command.Command> known,
				Plugin plugin, PluginCommand command) {
			String name = command.getName().toLowerCase(Locale.ENGLISH);
			String prefix = plugin.getName().toLowerCase(Locale.ENGLISH);
			List<String> successfulNames = new ArrayList<>();
			register(known, name, prefix, false, command, successfulNames);
			for (String alias : command.getAliases()) {
				register(known, alias, prefix, true, command, successfulNames);
			}
		}

		private void register(Map<String, org.bukkit.command.Command> known, String name,
				String prefix, boolean alias, org.bukkit.command.Command command,
				List<String> successfulNames) {
			String key = prefix + ":" + name;
			boolean work = false;
			if (known.containsKey(key)) {
				org.bukkit.command.Command ex = known.get(key);
				DarkCubeSystem.getInstance().getLogger().warning(
						"[CommandAPI] Failed to register command: Command with that name already exists");
				DarkCubeSystem.getInstance().getLogger()
						.warning("[CommandAPI] Command: " + key + " - " + ex);
				if (!alias) {
					command.setLabel(key);
				}
			} else {
				work = true;
			}
			if (work) {
				known.put(key, command);
				successfulNames.add(key);
			}
			work = false;
			if (known.containsKey(name)) {
				org.bukkit.command.Command ex = known.get(name);
				if (ex instanceof VanillaCommandWrapper) {
					work = true;
				} else {
					DarkCubeSystem.getInstance().getLogger().warning(
							"[CommandAPI] Failed to register command: Command with that name already exists");
					DarkCubeSystem.getInstance().getLogger()
							.warning("[CommandAPI] Command: " + name + " - " + ex);
				}
			} else {
				work = true;
			}
			if (work) {
				known.put(name, command);
				successfulNames.add(name);
			}
		}

		private void checkAmbiguities(String registerName,
				Map<String, org.bukkit.command.Command> knownCommands, PluginCommand command) {
			if (knownCommands.containsKey(registerName)) {
				org.bukkit.command.Command cmd = knownCommands.get(registerName);
				if (cmd instanceof PluginCommand) {
					PluginCommand pcmd = (PluginCommand) cmd;
					if (pcmd.getExecutor() == this.executor
							&& pcmd.getTabCompleter() == this.executor) {
						return;
					}
					Bukkit.getConsoleSender().sendMessage(
							"§6Overriding command " + registerName + " from Plugin "
									+ pcmd.getPlugin().getName() + "!");
				} else {
					Bukkit.getConsoleSender().sendMessage(
							"§6Overriding command " + registerName + " from type " + cmd.getClass()
									.getSimpleName() + "!");
				}
			}
			knownCommands.put(registerName, command);
		}

		private String sjoin(String label, String[] args) {
			return args.length == 0 ? label : (label + " " + String.join(" ", args));
		}

		private class Executor implements TabExecutor {

			@Override
			public List<String> onTabComplete(CommandSender sender,
					org.bukkit.command.Command command, String label, String[] args) {
				String commandLine = sjoin(label, args);
				String commandLine2 =
						sjoin(label, Arrays.copyOfRange(args, 0, args.length - 1)) + " ";
				List<Suggestion> completions =
						eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().getCommands()
								.getTabCompletions(sender, commandLine);
				ICommandExecutor executor = new BukkitCommandExecutor(sender);
				if (!completions.isEmpty()) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						int version = ViaSupport.getVersion(p);
						if (version < 393) { // 393 is the version for 1.13
							// https://minecraft.fandom.com/wiki/Protocol_version
							executor.sendMessage(Component.text(" "));
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
			public boolean onCommand(CommandSender sender, org.bukkit.command.Command command,
					String label, String[] args) {
				String commandLine = sjoin(label, args);
				eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().getCommands()
						.executeCommand(sender, commandLine);
				return true;
			}

		}
	}
}
