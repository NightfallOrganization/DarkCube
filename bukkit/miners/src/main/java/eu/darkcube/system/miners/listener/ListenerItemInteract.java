/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListenerItemInteract implements Listener {

	@EventHandler
	public void onItemInteract(PlayerInteractEvent e) {
		if (e.getMaterial().equals(Material.WORKBENCH)) {
			e.setCancelled(true);
			e.getPlayer().openWorkbench(null, true);
		}
	}

}
