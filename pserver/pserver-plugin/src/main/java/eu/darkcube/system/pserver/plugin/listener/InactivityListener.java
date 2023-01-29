/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.listener;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.system.pserver.plugin.PServerPlugin;

public class InactivityListener extends SingleInstanceBaseListener {

	private boolean isPlayerOnline = !Bukkit.getOnlinePlayers().isEmpty();
	private int task = -1;
	private Runnable runnable = () -> {
		System.out.println("Shutting down due to inactivity!");
		Bukkit.shutdown();
	};

	public InactivityListener() {
		start(null);
	}

	@EventHandler
	public void handle(PlayerQuitEvent event) {
		start(event.getPlayer());
	}

	private boolean start(Player exclusion) {
		isPlayerOnline = !Bukkit.getOnlinePlayers().stream().filter(p -> p != exclusion).collect(Collectors.toList()).isEmpty();
		if (!isPlayerOnline) {
			System.currentTimeMillis();
			task = Bukkit.getScheduler().runTaskLater(PServerPlugin.getInstance(), runnable, 20
							* TimeUnit.MINUTES.toSeconds(5)).getTaskId();
			return true;
		}
		return false;
	}

	@EventHandler
	public void handle(PlayerJoinEvent event) {
		isPlayerOnline = true;
		if (task != -1) {
			Bukkit.getScheduler().cancelTask(task);
			task = -1;
		}
	}
}
