/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;

public class ListenerJoin extends BaseListener {

	@EventHandler
	public void handle(PlayerJoinEvent e) {
		LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(e.getPlayer()));
		Lobby.getInstance().setupPlayer(user);
		user.playSound(Sound.FIREWORK_LAUNCH, .5F, 1);
	}
}
