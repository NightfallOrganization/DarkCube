package eu.darkcube.system.commandapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import eu.darkcube.system.ReflectionUtils;
import eu.darkcube.system.ReflectionUtils.PackageType;

public class CommandAPI {

//	private static final Class<?> CLS_VANILLA_COMMAND_WRAPPER = Reflection
//			.getVersionClass(ReflectionUtils.CRAFTBUKKIT_PREFIX, "command.VanillaCommandWrapper");
	private static final Class<?> CLS_VANILLA_COMMAND_WRAPPER = ReflectionUtils.getClass("VanillaCommandWrapper",
			PackageType.CRAFTBUKKIT_COMMAND);

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
	 * @param owner  - MUST be a Command with the name of the Bukkit-Command
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
		CommandAPI.registerCommands(plugin, owner);
		PluginCommand cmd = plugin.getCommand(owner.getName());

		CommandHandler handler = new CommandHandler(api);
		cmd.setAliases(Arrays.asList(owner.getAliases()));
		cmd.setExecutor(handler);
		cmd.setTabCompleter(handler);
		api.loadPermissions(api.main_command);
		for (String perm : api.getKnownPermissions()) {
			try {
				Bukkit.getPluginManager().addPermission(new Permission(perm));
			} catch (Exception ex) {
			}
		}
		return api;
	}

	@SuppressWarnings("unchecked")
	private static synchronized void registerCommands(JavaPlugin plugin, Command owner) {
		try {
			Set<Command> commandSet = new HashSet<>();
			commandSet.add(owner);
			for (Command command : commandSet) {
				Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class,
						Plugin.class);
				constructor.setAccessible(true);
				PluginCommand plugincommand = constructor.newInstance(command.getName(), plugin);
				plugincommand.setAliases(Arrays.asList(owner.getAliases()));
				plugincommand.setUsage(command.getSimpleLongUsage());
				plugincommand.setPermission(command.getPermission());
				Object server = Bukkit.getServer();
				Method methodGETCOMMANDMAP = server.getClass().getMethod("getCommandMap");
				CommandMap commandMap = (CommandMap) methodGETCOMMANDMAP.invoke(server);
				commandMap.register("system", plugincommand);
				Field f = commandMap.getClass().getDeclaredField("knownCommands");
				f.setAccessible(true);
				Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>) f
						.get(commandMap);
				if (CommandAPI.CLS_VANILLA_COMMAND_WRAPPER
						.isInstance(knownCommands.get(owner.getName().toLowerCase()))) {
					knownCommands.put(owner.getName().toLowerCase(), plugincommand);
				}
				for (String alias : owner.getAliases()) {
					if (CommandAPI.CLS_VANILLA_COMMAND_WRAPPER.isInstance(knownCommands.get(alias.toLowerCase()))) {
						knownCommands.put(alias.toLowerCase(), plugincommand);
					}
				}
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
	}

	public Set<Command> getDeepSubCommands(Command cmd) {
		Set<Command> subs = cmd.getChilds();
		for (Command sub : new HashSet<>(subs)) {
			subs.addAll(this.getDeepSubCommands(sub));
		}
		return subs;
	}

	private void loadPermissions(Command cmd) {
		Set<Command> childs = this.checkPermissionAndAdd(cmd);
		for (Command child : childs)
			this.loadPermissions(child);
	}

	private Set<Command> checkPermissionAndAdd(Command cmd) {
		if (!this.knownPermissions.contains(cmd.getPermission()))
			this.knownPermissions.add(cmd.getPermission());
		cmd.instance = this;
		return cmd.getChilds();
	}

	public Command getMainCommand() {
		return this.main_command;
	}

}
