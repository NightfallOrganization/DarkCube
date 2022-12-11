/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Message;

public class ListenerChat implements Listener {

	@EventHandler
	public void handle(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Team team = Team.getTeam(p);
		Main main = Main.getInstance();

		String msg = e.getMessage();
		boolean atall = false;
		boolean replace = !main.getLobby().isActive();
		boolean startsatall = false;
		if (main.getIngame().isActive()) {
			startsatall = msg.startsWith(main.atall);
			boolean startsatteam = msg.startsWith(main.atteam);
			atall = startsatall || team.getPlayers().size() == 1;
			if (msg.length() >= main.atall.length() + 1) {
				if ((startsatall && msg.substring(main.atall.length()).charAt(0) == ' ')
						|| (startsatteam && msg.substring(main.atall.length()).charAt(0) == ' ')) {
					msg = msg.replaceFirst(" ", "");
				}
			}
			if (startsatteam) {
				atall = false;
				msg = msg.substring(2);
			}
			if (team == Team.SPECTATOR) {
				atall = false;
			}
		}
		String color = ChatColor.getByChar(team.getNamecolor()).toString();
		e.setCancelled(true);
		if (msg.length() == 0 || (startsatall && msg.substring(2).length() == 0)) {
			return;
		}
		msg = getMessage(p, msg, atall, color, main, startsatall);
		if (team == Team.SPECTATOR) {
			main.sendMessageWithoutPrefix(msg, team.getPlayers());
			main.sendConsoleWithoutPrefix(msg);
		} else {
			for (Player t : Bukkit.getOnlinePlayers()) {
				String pmsg = msg;
				if (atall && replace) {
					pmsg = pmsg.replaceFirst(main.atall, Message.AT_ALL.getMessage(t));
					t.sendMessage(pmsg);
				} else if (Team.getTeam(t) == team) {
					t.sendMessage(pmsg);
				}
			}
			main.sendConsoleWithoutPrefix(msg);
		}
	}

	private String getMessage(Player p, String msg, boolean atall, String color, Main main, boolean satall) {
		return color + (atall && main.getIngame().isActive() ? main.atall : "") + color + p.getName() + ChatColor.RESET
				+ ": " + (satall ? msg.substring(main.atall.length()) : msg);
	}
}
