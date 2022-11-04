package eu.darkcube.system.pserver.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import eu.darkcube.system.pserver.plugin.PServerPlugin;

public interface BaseListener extends Listener {

	default void register() {
		Bukkit.getPluginManager().registerEvents(this, PServerPlugin.getInstance());
	}

	default void unregister() {
		HandlerList.unregisterAll(this);
	}
}
