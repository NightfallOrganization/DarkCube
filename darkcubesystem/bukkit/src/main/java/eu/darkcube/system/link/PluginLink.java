/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.link;

import eu.darkcube.system.DarkCubeSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public abstract class PluginLink extends Link {
	private final String pluginName;
	private final PluginLinkListener listener = new PluginLinkListener();

	public PluginLink(String pluginName) throws Throwable {
		super();
		this.pluginName = pluginName;
	}

	void registerListener() {
		Bukkit.getPluginManager().registerEvents(listener, DarkCubeSystem.systemPlugin());
	}

	void unregisterListener() {
		HandlerList.unregisterAll(listener);
	}

	private class PluginLinkListener implements Listener {
		@EventHandler
		public void handle(PluginDisableEvent event) {
			if (event.getPlugin().getName().equals(pluginName)) {
				disable();
			}
		}
	}
}
