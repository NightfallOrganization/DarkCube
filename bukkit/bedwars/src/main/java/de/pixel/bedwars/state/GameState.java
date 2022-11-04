package de.pixel.bedwars.state;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import de.pixel.bedwars.Main;

public abstract class GameState {

	private static GameState activeGamestate;
	private Listener[] listeners;

	public GameState() {
		this(new Listener[0]);
	}

	public GameState(Listener... listeners) {
		this.listeners = listeners;
	}

	public boolean activate() {
		if (activeGamestate != this) {
			if (activeGamestate != null)
				activeGamestate.deactivate();
			for (Listener listener : listeners) {
				Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
			}
			activeGamestate = this;
			this.onEnable();
			return true;
		}
		return false;
	}

	public void deactivate() {
		for (Listener listener : listeners) {
			HandlerList.unregisterAll(listener);
		}
		this.onDisable();
	}

	protected abstract void onEnable();

	protected abstract void onDisable();

	public boolean isActive() {
		return activeGamestate == this;
	}
	
	public static GameState getActiveGamestate() {
		return activeGamestate;
	}
}
