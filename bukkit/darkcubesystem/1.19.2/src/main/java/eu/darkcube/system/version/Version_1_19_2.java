/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import com.google.gson.JsonArray;
import com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.v3.BukkitCommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.util.ChatBaseComponent;
import eu.darkcube.system.util.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_19_R1.command.VanillaCommandWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unused")
public class Version_1_19_2 implements Version {
	private CommandAPI commandAPI;

	@Override
	public void init() {
		this.commandAPI = new CommandApi();
	}

	@Override
	public CommandAPI commandApi() {
		return commandAPI;
	}

	@Override
	public String getSpigotUnknownCommandMessage() {
		return SpigotConfig.unknownCommandMessage;
	}

	@Override
	public void sendMessage(ChatBaseComponent component, CommandSender... senders) {
		ChatUtils.ChatEntry[] entries = component.getEntries();
		JsonArray a = new JsonArray();
		for (ChatUtils.ChatEntry entry : entries) {
			a.add(entry.getJson());
		}
		Component c = GsonComponentSerializer.gson().deserialize(a.toString());
		for (CommandSender s : senders) {
			switch (component.getDisplay()) {
				case ACTIONBAR -> s.sendActionBar(c);
				case CHAT -> s.sendMessage(c);
				case TITLE -> {
					switch (component.getTitleType()) {
						case TITLE -> s.sendTitlePart(TitlePart.TITLE, c);
						case SUBTITLE -> s.sendTitlePart(TitlePart.SUBTITLE, c);
						case CLEAR -> s.clearTitle();
					}
				}
			}
		}
	}

	private static class CommandApi implements CommandAPI {
		private final Executor executor = new Executor();

		@Override
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
				final Map<String, org.bukkit.command.Command> knownCommands =
						commandMap.getKnownCommands();
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

		private void checkAmbiguities(String registerName,
				Map<String, org.bukkit.command.Command> knownCommands, PluginCommand command) {
			if (knownCommands.containsKey(registerName)) {
				org.bukkit.command.Command cmd = knownCommands.get(registerName);
				if (cmd instanceof PluginCommand pcmd) {
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


		@Override
		public void registerLegacy(Plugin plugin, Command owner) {
			Set<Command> commandSet = new HashSet<>();
			commandSet.add(owner);
			for (Command command : commandSet) {
				try {
					Constructor<PluginCommand> constructor =
							PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
					constructor.setAccessible(true);
					PluginCommand plugincommand =
							constructor.newInstance(command.getName(), plugin);
					plugincommand.setAliases(Arrays.asList(owner.getAliases()));
					plugincommand.setUsage(command.getSimpleLongUsage());
					plugincommand.setPermission(command.getPermission());
					CraftServer server = (CraftServer) Bukkit.getServer();
					CraftCommandMap commandMap = (CraftCommandMap) server.getCommandMap();
					commandMap.register("system", plugincommand);

					Map<String, org.bukkit.command.Command> knownCommands =
							commandMap.getKnownCommands();
					if (knownCommands.get(
							owner.getName().toLowerCase()) instanceof VanillaCommandWrapper) {
						knownCommands.put(owner.getName().toLowerCase(), plugincommand);
					}
					for (String alias : owner.getAliases()) {
						if (knownCommands.get(
								alias.toLowerCase()) instanceof VanillaCommandWrapper) {
							knownCommands.put(alias.toLowerCase(), plugincommand);
						}
					}
				} catch (IllegalAccessException | NoSuchMethodException |
						InvocationTargetException | InstantiationException e) {
					throw new RuntimeException(e);
				}
			}
		}

		@Override
		public double[] getEntityBB(Entity entity) {
			return new double[] {entity.getBoundingBox().getMinX(),
					entity.getBoundingBox().getMinY(), entity.getBoundingBox().getMinZ(),
					entity.getBoundingBox().getMaxX(), entity.getBoundingBox().getMaxY(),
					entity.getBoundingBox().getMaxZ()};
		}

		private class Executor implements TabExecutor {

			@Override
			public List<String> onTabComplete(@NotNull CommandSender sender,
					org.bukkit.command.@NotNull Command command, @NotNull String label,
					String[] args) {
				String commandLine = sjoin(label, args);
				String commandLine2 =
						sjoin(label, Arrays.copyOfRange(args, 0, args.length - 1)) + " ";
				List<Suggestion> completions =
						eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().getCommands()
								.getTabCompletions(sender, commandLine);
				ICommandExecutor executor = new BukkitCommandExecutor(sender);
				if (!completions.isEmpty()) {
					if (sender instanceof Player p) {
						int version = ViaSupport.getVersion(p);
						if (version < 393) { // 393 is the version for 1.13
							// https://minecraft.fandom.com/wiki/Protocol_version
							executor.sendMessage(new CustomComponentBuilder(" ").create());
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
			public boolean onCommand(@NotNull CommandSender sender,
					org.bukkit.command.@NotNull Command command, @NotNull String label,
					String[] args) {
				String commandLine = sjoin(label, args);
				eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().getCommands()
						.executeCommand(sender, commandLine);
				return true;
			}

		}
	}
}
