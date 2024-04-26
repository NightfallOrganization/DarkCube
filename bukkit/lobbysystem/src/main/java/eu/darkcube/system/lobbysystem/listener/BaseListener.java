/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import eu.darkcube.system.lobbysystem.Lobby;

public class BaseListener implements Listener {

	public BaseListener() {
		Lobby.getInstance().getServer().getPluginManager().registerEvents(this,
				Lobby.getInstance());
	}

	public void unregister() {
		HandlerList.unregisterAll(this);
	}
}
