/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerChangeBlock extends Listener<EntityChangeBlockEvent> {
	@Override
	@EventHandler
	public void handle(EntityChangeBlockEvent e) {
		if (e.getEntityType() == EntityType.FALLING_BLOCK) {
			if (e.getTo() == Material.WOOL) {
				WoolBattle.getInstance().getIngame().placedBlocks.add(e.getBlock());
			} else {
				e.setCancelled(true);
			}
		}
	}
}
