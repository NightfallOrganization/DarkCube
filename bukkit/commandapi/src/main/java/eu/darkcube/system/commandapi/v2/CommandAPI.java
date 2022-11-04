package eu.darkcube.system.commandapi.v2;

import org.bukkit.plugin.java.JavaPlugin;

public class CommandAPI {

	private JavaPlugin plugin;
	private ICommandMap commandMap;
	
	public CommandAPI(JavaPlugin plugin) {
		this.plugin = plugin;
		this.commandMap = new DefaultCommandMap(this.plugin);
	}
	
	public ICommandMap getCommandMap() {
		return commandMap;
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
}
