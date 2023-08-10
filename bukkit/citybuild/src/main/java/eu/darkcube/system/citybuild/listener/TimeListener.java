/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class TimeListener implements Listener {
	private static final String WORLD_NAME = "Beastrealm";

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		if (world.getName().equals(WORLD_NAME)) {
			player.setPlayerTime(6000L, false);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		if (world.getName().equals(WORLD_NAME)) {
			player.setPlayerTime(6000L, false);
		}
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		if (world.getName().equals(WORLD_NAME)) {
			player.setPlayerTime(6000L, false);
		}
	}
}
