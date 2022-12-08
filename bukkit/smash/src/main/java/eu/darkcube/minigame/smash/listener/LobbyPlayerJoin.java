/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.darkcube.minigame.smash.Main;
import eu.darkcube.minigame.smash.state.lobby.Lobby;
import eu.darkcube.minigame.smash.user.User;
import eu.darkcube.minigame.smash.user.UserWrapper;

public class LobbyPlayerJoin extends BaseListener {

	@EventHandler
	public void handle(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		User user = UserWrapper.getUser(p);
		Lobby lobby = Main.getInstance().getLobby();
		lobby.loadScoreboard(user);
		lobby.teleport(user);
		lobby.setItems(user);
//		user.setItems();
	}
}
