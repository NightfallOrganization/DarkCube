/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;

public class ListenerLaunchable implements Listener {

	private Set<Player> executing = new HashSet<>();

	@EventHandler
	public void handle(ProjectileLaunchEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity().getShooter();
		if (this.executing.contains(p)) {
			return;
		}
		executing.add(p);
		LaunchableInteractEvent pe =
				new LaunchableInteractEvent(p, e.getEntity(), p.getItemInHand());
		Bukkit.getPluginManager().callEvent(pe);
		if (pe.isCancelled()) {
			e.setCancelled(true);
		}
		executing.remove(p);
	}

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if (e.getItem() == null || e.getItem().getType() == Material.AIR) {
			return;
		}
		EntityType type = null;
		switch (e.getItem().getType()) {
			case ENDER_PEARL:
				type = EntityType.ENDER_PEARL;
				break;
			case EYE_OF_ENDER:
				type = EntityType.ENDER_SIGNAL;
				break;
			case EGG:
				type = EntityType.EGG;
				break;
			case SNOW_BALL:
				type = EntityType.SNOWBALL;
				break;
			case EXP_BOTTLE:
				type = EntityType.THROWN_EXP_BOTTLE;
				break;
			default:
				break;
		}
		if (this.executing.contains(e.getPlayer())) {
			return;
		}
		this.executing.add(e.getPlayer());
		LaunchableInteractEvent pe =
				new LaunchableInteractEvent(e.getPlayer(), type, e.getItem(), e.getAction());
		Bukkit.getPluginManager().callEvent(pe);
		if (pe.isCancelled()) {
			e.setCancelled(true);
		}
		executing.remove(e.getPlayer());
	}

}
