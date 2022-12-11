/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class IngameEntityDamage extends BaseListener {

	@EventHandler
	public void handle(EntityDamageEvent e) {
		if(e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		}
	}
}
