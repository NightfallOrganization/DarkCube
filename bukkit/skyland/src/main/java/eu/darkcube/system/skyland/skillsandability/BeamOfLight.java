/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.skillsandability;

import eu.darkcube.system.skyland.Skyland;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class BeamOfLight extends Skills {
	@Override
	public void activate(Player p) {

		if (isReady(p.getWorld())) {
			//p.setVelocity(p.getLocation().getDirection().multiply(3));

			Vector vec = p.getLocation().getDirection();
			final int[] counter = {0};
			final Location[] loc = {p.getLocation()};
			new BukkitRunnable() {
				Player source  = p;
				@Override
				public void run() {

					if (counter[0] < 100) {
						DustOptions dustOptions = new DustOptions(Color.YELLOW, 10);

						p.spawnParticle(Particle.REDSTONE, loc[0], 3, dustOptions);
						Collection<LivingEntity> nearby = loc[0].getNearbyLivingEntities(1);
						nearby.remove(source);
						if (!nearby.isEmpty()){
							for (LivingEntity living : loc[0].getNearbyLivingEntities(1)){
								living.setHealth(living.getHealth() + 6);
								spawnFireWork(living.getLocation().add(0, 2, 0), Color.YELLOW, 2);

							}
							cancel();
						}

						loc[0] = loc[0].add(vec);
						counter[0]++;
					} else {
						p.sendMessage("beam done");
						cancel();
					}
				}
			}.runTaskTimer(Skyland.getInstance(), 1, 2);

			addCooldown(p.getWorld(), 100);
		} else {
			p.sendMessage("beam not ready");
		}
	}


}
