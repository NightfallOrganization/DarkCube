/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.lobby;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerPlayerQuit extends Listener<PlayerQuitEvent> {

	@Override
	@EventHandler
	public void handle(PlayerQuitEvent e) {
		WBUser user = WBUser.getUser(e.getPlayer());
		Lobby lobby = WoolBattle.instance().getLobby();
		lobby.getScoreboardByUser().remove(user);
		lobby.VOTES_MAP.remove(user);
		lobby.VOTES_EP_GLITCH.remove(user);
		lobby.VOTES_LIFES.remove(user);
		WoolBattle.instance().getLobby().recalculateMap();
		WoolBattle.instance().getLobby().recalculateEpGlitch();

		WoolBattle.instance().sendMessage(Message.PLAYER_LEFT, user.getTeamPlayerName());

		WBUser.onlineUsers().forEach(t -> WoolBattle.instance().setOnline(t));
		e.setQuitMessage(null);
	}
}
