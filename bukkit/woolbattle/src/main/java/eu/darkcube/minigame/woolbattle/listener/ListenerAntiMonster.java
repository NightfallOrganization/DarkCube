/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class ListenerAntiMonster implements Listener {

	@EventHandler
	public void handle(EntitySpawnEvent event) {
		if (event.getEntity() instanceof Monster) {
			event.setCancelled(true);
		}
	}
}
