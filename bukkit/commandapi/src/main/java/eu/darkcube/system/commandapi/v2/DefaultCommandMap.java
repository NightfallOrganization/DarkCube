package eu.darkcube.system.commandapi.v2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import eu.darkcube.system.ReflectionUtils;
import eu.darkcube.system.ReflectionUtils.PackageType;

public class DefaultCommandMap implements ICommandMap {

	private static final Class<?> CLS_VANILLA_COMMAND_WRAPPER = ReflectionUtils.getClass("VanillaCommandWrapper",
			PackageType.CRAFTBUKKIT_COMMAND);

	private final Map<String, Command> registeredCommands = new ConcurrentHashMap<>();

	private final JavaPlugin plugin;

	public DefaultCommandMap(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public Collection<Command> getCommands() {
		return new HashSet<>(this.registeredCommands.values());
	}

	@SuppressWarnings("unchecked")
	private void pluginRegisterCommand(Command command) {
		try {
			Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class,
					Plugin.class);
			constructor.setAccessible(true);
			PluginCommand plugincommand = constructor.newInstance(command.getNames()[0], this.plugin);
			String name = command.getNames()[0];
			String[] aliases = Arrays.copyOfRange(command.getNames(), 1, command.getNames().length);
			plugincommand.setAliases(Arrays.asList(aliases));
			plugincommand.setUsage("/" + name);
			plugincommand.setPermission(command.getPermission());
			plugincommand.setExecutor(new CommandExecutor() {

				@Override
				public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label,
						String[] args) {
					String commandLine = label + " " + String.join(" ", args);
					DefaultCommandMap.this.dispatchCommand(new BukkitCommandExecutor(sender), commandLine);
					return true;
				}

			});
			plugincommand.setTabCompleter(new TabCompleter() {

				@Override
				public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command,
						String label, String[] args) {
					String commandLine = label + " " + String.join(" ", args);
					return DefaultCommandMap.this.tabCompleteCommand(commandLine);
				}

			});
			Object server = Bukkit.getServer();
			Method methodGETCOMMANDMAP = server.getClass().getMethod("getCommandMap");
			CommandMap commandMap = (CommandMap) methodGETCOMMANDMAP.invoke(server);
			commandMap.register("system", plugincommand);
			Field f = commandMap.getClass().getDeclaredField("knownCommands");
			f.setAccessible(true);
			Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>) f
					.get(commandMap);
			for (String n : command.getNames()) {
				if (DefaultCommandMap.CLS_VANILLA_COMMAND_WRAPPER.isInstance(knownCommands.get(n.toLowerCase()))) {
					knownCommands.put(n.toLowerCase(), plugincommand);
				}
			}
			if (command.getPermission() != null) {
				if (Bukkit.getPluginManager().getPermission(command.getPermission()) == null) {
					Bukkit.getPluginManager().addPermission(new Permission(command.getPermission()));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void registerCommand(Command command) {
		if (command != null) {
			for (String name : command.getNames()) {
				this.registeredCommands.put(name.toLowerCase(), command);

				this.pluginRegisterCommand(command);

				if (command.getPrefix() != null && !command.getPrefix().isEmpty()) {
					this.registeredCommands.put(command.getPrefix().toLowerCase() + ":" + name.toLowerCase(), command);
				}
			}
		}
	}

	@Override
	public void unregisterCommand(Command command) {
		Collection<String> names = new ArrayList<>();
		for (String key : this.registeredCommands.keySet()) {
			if (this.registeredCommands.get(key).equals(command)) {
				names.add(key);
			}
		}
		names.forEach(this.registeredCommands::remove);
	}

	@Override
	public void unregisterCommand(Class<? extends Command> command) {

		for (Command commandEntry : this.registeredCommands.values()) {
			if (commandEntry.getClass().equals(command)) {
				for (String commandName : commandEntry.getNames()) {
					this.registeredCommands.remove(commandName.toLowerCase());

					if (commandEntry.getPrefix() != null && !commandEntry.getPrefix().isEmpty()) {
						this.registeredCommands
								.remove(commandEntry.getPrefix().toLowerCase() + ":" + commandName.toLowerCase());
					}
				}
			}
		}
	}

	@Override
	public void unregisterCommands(ClassLoader classLoader) {

		for (Command commandEntry : this.registeredCommands.values()) {
			if (commandEntry.getClass().getClassLoader().equals(classLoader)) {
				for (String commandName : commandEntry.getNames()) {
					this.registeredCommands.remove(commandName.toLowerCase());

					if (commandEntry.getPrefix() != null && !commandEntry.getPrefix().isEmpty()) {
						this.registeredCommands
								.remove(commandEntry.getPrefix().toLowerCase() + ":" + commandName.toLowerCase());
					}
				}
			}
		}
	}

	public Collection<String> getCommandNames() {
		return this.registeredCommands.keySet();
	}

	@Override
	public void unregisterCommands() {
		this.registeredCommands.clear();
	}

	@Override
	public List<String> tabCompleteCommand(String commandLine) {
		if (commandLine.isEmpty() || commandLine.indexOf(' ') == -1) {
			return this.getCommandNames()
					.stream()
					.filter(name -> name != null && name.toLowerCase().startsWith(commandLine.toLowerCase()))
					.collect(Collectors.toList());
		}
		Command command = this.getCommandFromLine(commandLine);

		if (command instanceof ITabCompleter) {
			String[] args = this.formatArgs(commandLine.split(" "));
			String testString = args.length <= 1 || commandLine.endsWith(" ") ? ""
					: args[args.length - 1].toLowerCase().trim();
			if (commandLine.endsWith(" ")) {
				args = Arrays.copyOfRange(args, 1, args.length + 1);
				args[args.length - 1] = "";
			} else {
				args = Arrays.copyOfRange(args, 1, args.length);
			}

			Collection<String> responses = ((ITabCompleter) command).tabComplete(commandLine, args,
					PropertyHelper.parse(args));
			if (responses != null && !responses.isEmpty()) {
				return responses.stream()
						.filter(response -> response != null
								&& (testString.isEmpty() || response.toLowerCase().startsWith(testString)))
						.map(response -> response.contains(" ") ? "\"" + response + "\"" : response)
						.collect(Collectors.toList());
			}
		}
		return Collections.emptyList();
	}

	@Override
	public List<String> tabCompleteCommand(String[] args, Properties properties) {
		if (args.length == 0) {
			return new ArrayList<>(this.getCommandNames());
		}

		Command command = this.getCommand(args[0]);

		if (command == null) {
			return this.getCommandNames()
					.stream()
					.filter(name -> name != null && name.toLowerCase().startsWith(args[0].toLowerCase()))
					.collect(Collectors.toList());
		}

		if (command instanceof ITabCompleter) {

			String testString = args[args.length - 1].toLowerCase().trim();

			Collection<String> responses = ((ITabCompleter) command).tabComplete(String.join(" ", args),
					Arrays.copyOfRange(args, 1, args.length), properties);
			if (responses != null && !responses.isEmpty()) {
				return responses.stream()
						.filter(response -> response != null
								&& (testString.isEmpty() || response.toLowerCase().startsWith(testString)))
						.collect(Collectors.toList());
			}
		}

		return Collections.emptyList();
	}

	@Override
	public Command getCommand(String name) {
		if (name == null) {
			return null;
		}

		return this.registeredCommands.get(name.toLowerCase());
	}

	@Override
	public Command getCommandFromLine(String commandLine) {
		if (commandLine == null || commandLine.isEmpty()) {
			return null;
		}

		String[] a = this.formatArgs(commandLine.split(" "));
		return a.length >= 1 ? this.registeredCommands.get(a[0].toLowerCase()) : null;
	}

	@Override
	public boolean dispatchCommand(ICommandExecutor commandSender, String commandLine) {
		if (commandSender == null || commandLine == null || commandLine.trim().isEmpty()) {
			return false;
		}

		boolean response = true;

		String[] commands = commandLine.split(" && ");

		for (String command : commands) {
			response = response && this.dispatchCommand0(commandSender, command);
		}

		return response;
	}

	public boolean dispatchCommand0(ICommandExecutor commandSender, String commandLine) {
		String[] args = this.formatArgs(commandLine.split(" "));

		if (!this.registeredCommands.containsKey(args[0].toLowerCase())) {
			return false;
		}

		Command command = this.registeredCommands.get(args[0].toLowerCase());
		String commandName = args[0].toLowerCase();

		if (command.getPermission() != null && !commandSender.hasPermission(command.getPermission())) {
			return false;
		}

		args = Arrays.copyOfRange(args, 1, args.length);

		try {
			command.execute(commandSender, commandName, args, commandLine, PropertyHelper.parse(args));
			return true;

		} catch (Throwable throwable) {
			throw new RuntimeException(commandLine, throwable);
		}
	}

	private String[] formatArgs(String[] args) {
		Collection<String> newArgs = new ArrayList<>();
		StringBuilder current = new StringBuilder();

		for (String arg : args) {
			boolean starts = arg.startsWith("\"");
			boolean ends = arg.endsWith("\"");

			if (starts || current.length() > 0) {

				if (starts) {
					arg = arg.substring(1);
				}

				current.append(arg);

				if (ends) {
					newArgs.add(current.substring(0, current.length() - 1));
					current.setLength(0);
				} else {
					current.append(' ');
				}

			} else {
				newArgs.add(arg);
			}
		}

		if (current.length() > 0) {
			newArgs.add(current.toString());
		}

		return newArgs.toArray(new String[0]);
	}

}
