/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerPlayerQuit extends Listener<PlayerQuitEvent> {

	@Override
	@EventHandler
	public void handle(PlayerQuitEvent e) {
		User user = WoolBattle.getInstance().getUserWrapper().getUser(e.getPlayer().getUniqueId());
		Lobby lobby = WoolBattle.getInstance().getLobby();
		lobby.getScoreboardByUser().remove(user);
		lobby.VOTES_MAP.remove(user);
		lobby.VOTES_EP_GLITCH.remove(user);
		lobby.VOTES_LIFES.remove(user);
		WoolBattle.getInstance().getLobby().recalculateMap();
		WoolBattle.getInstance().getLobby().recalculateEpGlitch();

		for (User t : WoolBattle.getInstance().getUserWrapper().getUsers()) {
			t.getBukkitEntity().sendMessage(Message.PLAYER_LEFT.getMessage(t, user.getTeamPlayerName()));
		}
		WoolBattle.getInstance().sendConsole(Message.PLAYER_LEFT.getServerMessage(user.getTeamPlayerName()));

		WoolBattle.getInstance().getUserWrapper().getUsers().forEach(t -> WoolBattle.getInstance().setOnline(t));
		e.setQuitMessage(null);
	}
}
