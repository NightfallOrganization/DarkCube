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
import org.bukkit.event.player.PlayerJoinEvent;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.state.Ingame;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Message;

public class IngamePlayerJoin implements Listener {

	@EventHandler
	public void handle(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Player p = e.getPlayer();
		Ingame ingame = Main.getInstance().getIngame();
		Team team = Team.SPECTATOR;
		boolean reconnected = false;
		if (ingame.disconnectedPlayers.containsKey(p.getUniqueId())) {
			team = ingame.disconnectedPlayers.remove(p.getUniqueId());
			reconnected = true;
		}
		team.addPlayer0(p);
		Main.getInstance().getIngame().setupPlayer(p);
		Main.getInstance().getIngame().setScoreboardValues();
		
		if (reconnected) {
			Team t2 = team;
			Main.sendMessage(Message.INGAME_PLAYER_RECONNECTED,
					t -> "§" + t2.getNamecolor() + p.getName());
		}
	}
}
