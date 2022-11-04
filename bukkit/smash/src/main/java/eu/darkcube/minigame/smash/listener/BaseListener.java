package eu.darkcube.minigame.smash.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import eu.darkcube.minigame.smash.Main;

public abstract class BaseListener implements Listener {

	private boolean registered = false;

	public BaseListener() {
	}

	public final void register() {
		if (!registered) {
			registered = true;
			Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		}
	}

	public final void unregister() {
		if (registered) {
			registered = false;
			HandlerList.unregisterAll(this);
		}
	}
}
