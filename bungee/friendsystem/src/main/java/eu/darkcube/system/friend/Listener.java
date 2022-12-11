/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import eu.darkcube.system.friend.command.CommandMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class Listener implements net.md_5.bungee.api.plugin.Listener {

	@EventHandler
	public void handle(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		List<ProxiedPlayer> keys = new ArrayList<>();
		for (Entry<ProxiedPlayer, ProxiedPlayer> s : CommandMessage.reactions.entrySet()) {
			if (s.getKey().equals(p) || s.getValue().equals(p)) {
				keys.add(s.getKey());
			}
		}
		for (ProxiedPlayer key : keys) {
			CommandMessage.reactions.remove(key);
		}
		for (UUID uuid : FriendUtil.getFriends(p.getUniqueId())) {
			ProxiedPlayer t = Main.getInstance().getProxy().getPlayer(uuid);
			if (t != null) {
				Main.sendMessage(t, "§6" + p.getName() + " §cist nun offline!");
			}
		}
	}

	@EventHandler
	public void handle(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		List<ProxiedPlayer> ls = new ArrayList<>();
		for (UUID uuid : FriendUtil.getFriends(p.getUniqueId())) {
			ProxiedPlayer t = Main.getInstance().getProxy().getPlayer(uuid);
			if (t != null) {
				Main.sendMessage(t, "§6" + p.getName() + " §aist nun online!");
				ls.add(t);
			}
		}
		Main.getInstance().getProxy().getScheduler().schedule(Main.getInstance(), () -> {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < ls.size(); i++) {
				ProxiedPlayer t = ls.get(i);
				if (i > 5) {
//					b.append(" §7und §6" + (ls.size() - 5) + " §7andere deiner Freunde");
					Main.sendMessage(p,
							b.toString() + " §7und §6" + (ls.size() - 5) + " §7andere deiner Freunde sind online!");
					break;
				}
				b.append("§6" + t.getName());
				if (i + 1 != ls.size()) {
					b.append("§7, ");
				} else {
					if (ls.size() == 1) {
						Main.sendMessage(p, b.toString() + " §7ist online!");
					} else {
						Main.sendMessage(p, b.toString() + " §7sind online!");
					}
					break;
				}
			}
		}, 1, TimeUnit.SECONDS);
		
	}
}
