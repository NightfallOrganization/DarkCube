/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FarmlandListener implements Listener {
	@EventHandler
	public void handle(EntityInteractEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.FARMLAND) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handle(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL)
			return;
		Block block = event.getClickedBlock();
		if (block != null && block.getType() == Material.FARMLAND) {
			event.setCancelled(true);
		}
	}
}
