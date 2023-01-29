/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerBlockPlace extends Listener<BlockPlaceEvent> {

	@Override
	@EventHandler
	public void handle(BlockPlaceEvent e) {
		e.setCancelled(true);
	}
}
