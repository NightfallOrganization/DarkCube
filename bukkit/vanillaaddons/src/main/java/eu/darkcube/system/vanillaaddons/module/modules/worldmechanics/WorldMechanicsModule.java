/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.worldmechanics;

import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WorldMechanicsModule implements Module, Listener {
	private final VanillaAddons addons;

	public WorldMechanicsModule(VanillaAddons addons) {
		this.addons = addons;
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, addons);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}

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
