/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.skillsandability;

import eu.darkcube.system.skyland.Skyland;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public abstract class Skills {

	long cooldownReady = 0;
	//todo make persistent if duckness wants im unsure if it should be

	public abstract void activate(Player p);

	public void addListeners(Listener listener) {
		Skyland.getInstance().getServer().getPluginManager()
				.registerEvents(listener, Skyland.getInstance());
	}

	public void addCooldown(World world, int ticks) {
		cooldownReady = world.getFullTime() + ticks;
	}

	public boolean isReady(World world) {
		if (cooldownReady < world.getFullTime()) {
			return true;
		}
		return false;
	}

	public void spawnFireWork(Location loc, Color color, int power) {
		Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		fwm.setPower(power);
		fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(false).build());

		fw.setFireworkMeta(fwm);
		fw.detonate();
	}
	//todo

}
