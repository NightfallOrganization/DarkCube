/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.smash.util.scheduler.Scheduler;

public class IngameMoveClimbing extends BaseListener {

	public static final Material WALL_CLIMB_BLOCK = Material.LAPIS_BLOCK;

	private Set<Player> climbing = new HashSet<>();
	private Set<Player> sneaking = new HashSet<>();
	private Map<Player, Block> barrier = new HashMap<>();

	@EventHandler
	public void handle(PlayerMoveEvent e) {
		CraftPlayer p = (CraftPlayer) e.getPlayer();
		if (sneaking.contains(p)) {
			Location loc = e.getTo();
			Location clone = loc.clone();
			clone.add(loc.getDirection().setY(0).normalize().multiply(.5));
			if (clone.getBlock().getType() == WALL_CLIMB_BLOCK && !climbing.contains(p)) {
				climbing.add(p);
				new Scheduler() {
					@Override
					public void run() {
						Optional.ofNullable(barrier.remove(p)).ifPresent(block -> {
							if (block != p.getLocation().subtract(0, 1, 0).getBlock()) {
								send(p, block, Material.AIR);
							}
						});
						if (!climbing.contains(p)) {
							cancel();
							return;
						}
						Location l = p.getLocation().subtract(0, 1, 0);
						if (l.getBlock().getType() == Material.AIR) {
							send(p, l.getBlock(), Material.BARRIER);
							barrier.put(p, l.getBlock());
						}
					}
				}.runTaskTimer(1, 1);
			} else if (clone.getBlock().getType() != WALL_CLIMB_BLOCK && climbing.contains(p)) {
				climbing.remove(e.getPlayer());
				clone.add(loc.getDirection().setY(0).normalize().multiply(.5));
				if (clone.getBlock().getType() != WALL_CLIMB_BLOCK) {
					e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().setY(0).normalize()
							.multiply(.7).add(new Vector(0, 1, 0)).multiply(1));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void send(Player p, Block block, Material mat) {
		p.sendBlockChange(block.getLocation(), mat.getId(), (byte) 0);
	}

	@EventHandler
	public void handle(PlayerToggleSneakEvent e) {
		if (e.isSneaking()) {
			sneaking.add(e.getPlayer());
		} else {
			sneaking.remove(e.getPlayer());
			if (climbing.contains(e.getPlayer())) {
				climbing.remove(e.getPlayer());
			}
		}
	}
}
