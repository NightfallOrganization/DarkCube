/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerChat extends Listener<AsyncPlayerChatEvent> {

	@Override
	@EventHandler
	public void handle(AsyncPlayerChatEvent e) {
		WoolBattle main = WoolBattle.getInstance();
		Player p = e.getPlayer();
		User user = main.getUserWrapper().getUser(p.getUniqueId());
		String msg = e.getMessage();
		boolean atall = false;
		boolean replace = !main.getLobby().isEnabled();
		boolean startsatall = false;
		if (main.getIngame().isEnabled()) {
			startsatall = msg.startsWith(main.atall);
			boolean startsatteam = msg.startsWith(main.atteam);
			atall = startsatall || user.getTeam().getUsers().size() == 1;
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
			if (user.getTeam().getType().equals(TeamType.SPECTATOR)) {
				atall = false;
			}
		}
		String color = ChatColor.getByChar(user.getTeam().getType().getNameColor()).toString();
		e.setCancelled(true);
		if (msg.length() == 0 || (startsatall && msg.substring(2).length() == 0)) {
			return;
		}
		msg = getMessage(p, msg, atall, color, main, startsatall);
		if (user.getTeam().getType().equals(TeamType.SPECTATOR)) {
			main.sendMessageWithoutPrefix(msg, user.getTeam().getUsers().stream()
					.map(u -> Bukkit.getPlayer(u.getUniqueId())).collect(Collectors.toSet()));
			main.sendConsoleWithoutPrefix(msg);
		} else {
			for (User t : WoolBattle.getInstance().getUserWrapper().getUsers()) {
				String pmsg = msg;
				if (atall && replace) {
					pmsg = pmsg.replaceFirst(main.atall, Message.AT_ALL.getMessage(t));
					t.getBukkitEntity().sendMessage(pmsg);
				} else if (t.getTeam().getType().equals(user.getTeam().getType())) {
					t.getBukkitEntity().sendMessage(pmsg);
				}
			}
			main.sendConsoleWithoutPrefix(msg);
		}
	}

	private String getMessage(Player p, String msg, boolean atall, String color, WoolBattle main, boolean satall) {
		return color + (atall && main.getIngame().isEnabled() ? main.atall : "") + color + p.getName() + ChatColor.RESET
				+ ": " + (satall ? msg.substring(main.atall.length()) : msg);
	}

}
