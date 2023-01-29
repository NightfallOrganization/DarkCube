/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.endgame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerPlayerJoin extends Listener<PlayerJoinEvent> {
	@Override
	@EventHandler
	public void handle(PlayerJoinEvent e) {
		WoolBattle main = WoolBattle.getInstance();
		Player p = e.getPlayer();
		User user = main.getUserWrapper().getUser(p.getUniqueId());
		main.getEndgame().setPlayerItems(user);
		e.setJoinMessage(null);
		p.setAllowFlight(false);
		p.teleport(main.getLobby().getSpawn());
	}
}
