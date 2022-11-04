package eu.darkcube.system.commandapi;

import org.bukkit.plugin.java.JavaPlugin;

import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.arguments.EntityOptions;

public class CommandAPIPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		EntityOptions.registerOptions();
		CommandAPI.init(this);
	}

	@Override
	public void onDisable() {
		CommandAPI.getInstance().unregisterAll();
	}
}
