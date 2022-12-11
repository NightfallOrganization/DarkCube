/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.state.Lobby;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Message;

public class LobbyPlayerQuit implements Listener {

	@EventHandler
	public void handle(PlayerQuitEvent e) {
		Lobby lobby = Main.getInstance().getLobby();
		Player p = e.getPlayer();
		lobby.VOTE_GOLD.remove(p);
		lobby.recalculateGold();
		lobby.recalculateItemsVotingGoldInventories();

		lobby.VOTE_IRON.remove(p);
		lobby.recalculateIron();
		lobby.recalculateItemsVotingIronInventories();

		lobby.VOTE_MAP.remove(p);
		lobby.recalculateMap();
		lobby.recalculateItemsVotingMapsInventories();

		e.setQuitMessage(null);
		Main.sendMessage(Message.LOBBY_PLAYER_LEFT, t -> "§" + Team.getTeam(t).getNamecolor() + t.getName());
//		for (Player t : Bukkit.getOnlinePlayers()) {
//			t.sendMessage(Message.LOBBY_PLAYER_LEFT.getMessage(t, "§" + Team.getTeam(p).getNamecolor() + p.getName()));
//		}

		Team.getTeam(p).removePlayer0(p);
	}
}
