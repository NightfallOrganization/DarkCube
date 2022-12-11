/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.smash.util.scheduler.Scheduler;

public class IngameSneakThroughBlocks extends BaseListener {

	private Map<Player, Scheduler> runnables = new HashMap<>();

	@EventHandler
	public void handle(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if (e.isSneaking()) {
			runnables.put(p, new Scheduler() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					if (p.getLocation().getBlock().getType() == Material.AIR && p.isOnGround()) {
						Material mat = p.getLocation().subtract(0, 1, 0).getBlock().getType();
						if (mat == Material.STAINED_GLASS || mat == Material.GLASS) {
							p.teleport(p.getLocation().subtract(0, 0.2, 0));
							p.setVelocity(new Vector(0, -.2, 0));
						}
					}
				}
			});
			runnables.get(p).runTaskTimer(0, 2);
		} else {
			runnables.remove(p).cancel();
		}
	}
}
