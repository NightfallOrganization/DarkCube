/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
