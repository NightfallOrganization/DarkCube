/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;

public class ListenerQuit extends BaseListener {

	@EventHandler
	public void handle(PlayerQuitEvent e) {
		LobbyUser u = UserWrapper.fromUser(UserAPI.getInstance().getUser(e.getPlayer()));
		Lobby.getInstance().savePlayer(u);
	}
}
