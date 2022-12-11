/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerInteractEvent;

public class EndgameListener implements Listener {

	@EventHandler
	public void handle(EntityDamageEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void handle(BlockPlaceEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void handle(BlockBreakEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void handle(AsyncPlayerPreLoginEvent e) {
		e.setLoginResult(Result.KICK_OTHER);
		e.setKickMessage("§cDieser Server stoppt gerade.");
	}
}
