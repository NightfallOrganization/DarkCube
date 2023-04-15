/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.PacketOnlinePlayersSet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {
	private int online = 0;

	@EventHandler
	public void handle(PlayerJoinEvent event) {
		online++;
		new PacketOnlinePlayersSet(PServerProvider.instance().currentPServer().id(),
				online).sendAsync();
	}

	@EventHandler
	public void handle(PlayerQuitEvent event) {
		online--;
		new PacketOnlinePlayersSet(PServerProvider.instance().currentPServer().id(),
				online).sendAsync();
	}
}
