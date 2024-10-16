/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.state.Lobby;

public class LobbyInventoryClose implements Listener {

	@EventHandler
	public void handle(InventoryCloseEvent e) {
		Lobby l = Main.getInstance().getLobby();
		l.INVENTORY_OPEN_GOLD.remove(e.getPlayer());
		l.INVENTORY_OPEN_MAPS.remove(e.getPlayer());
		l.INVENTORY_OPEN_IRON.remove(e.getPlayer());
		l.INVENTORY_OPEN_TEAMS.remove(e.getPlayer());
	}
}
