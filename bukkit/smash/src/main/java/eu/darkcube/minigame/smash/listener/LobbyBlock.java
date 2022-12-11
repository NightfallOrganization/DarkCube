/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class LobbyBlock extends BaseListener {

	@EventHandler
	public void handle(BlockPlaceEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockBreakEvent e) {
		e.setCancelled(true);
	}
}
