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
import de.pixel.bedwars.state.Ingame;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Message;

public class IngamePlayerQuit implements Listener {

	@EventHandler
	public void handle(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		Player p = e.getPlayer();
		Ingame ingame = Main.getInstance().getIngame();
		Team team = Team.getTeam(p);
		if (Team.getTeams().contains(team)) {
			ingame.kill(p);
			Main.sendMessage(Message.INGAME_PLAYER_LEFT, t -> "§" + team.getNamecolor() + p.getName());
			ingame.disconnectedPlayers.put(p.getUniqueId(), team);
			ingame.checkPlayerOffline(team, p.getUniqueId());
		}
		team.removePlayer(p);
	}
}
