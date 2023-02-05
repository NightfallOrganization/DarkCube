/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.listener;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CreeperListener implements Listener {
	@EventHandler
	public void onCreeperExplosion(EntityExplodeEvent e) {
		if (e.getEntityType().equals(EntityType.CREEPER)) {
			e.setCancelled(true);
			Entity creeper = e.getEntity();
			creeper.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, creeper.getLocation(), 1);
			creeper.getWorld()
					.playSound(creeper.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 0.0F);
		}
	}
}
