/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerPlayerLogin extends Listener<AsyncPlayerPreLoginEvent>{

	@Override
	@EventHandler
	public void handle(AsyncPlayerPreLoginEvent e) {
		e.allow();
	}
	
	@EventHandler
	public void handle(PlayerLoginEvent e) {
		e.allow();
	}
}
