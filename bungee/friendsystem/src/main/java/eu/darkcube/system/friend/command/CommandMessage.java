/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.friend.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import eu.darkcube.system.friend.FriendUtil;
import eu.darkcube.system.friend.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CommandMessage extends Command implements TabExecutor {

	public static CommandMessage instance;
	public static Map<ProxiedPlayer, ProxiedPlayer> reactions = new HashMap<>();
	
	public CommandMessage() {
		super("message", "bungeecord.command.message", new String[] { "msg", "tell", "m" });
		instance = this;
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if (args.length == 1) {
				List<String> list = new ArrayList<>();
				String player = args[0];
				Set<UUID> friends = FriendUtil.getFriends(p.getUniqueId());
				for (ProxiedPlayer t : Main.getInstance().getProxy().getPlayers()) {
					if (friends.contains(t.getUniqueId())) {
						if (t.getName().toLowerCase().startsWith(player.toLowerCase())) {
							list.add(t.getName());
						}
					}
				}
				return list;
			}
		}
		return new ArrayList<>();
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if (args.length >= 2) {
				String player = args[0];
				Set<UUID> friends = FriendUtil.getFriends(p.getUniqueId());
				ProxiedPlayer t = null;
				for (ProxiedPlayer target : Main.getInstance().getProxy().getPlayers()) {
					if (target.getName().equalsIgnoreCase(player)) {
						t = target;
						break;
					}
				}
				if (t == null) {
					Main.sendMessage(p, "§cSpieler konnte nicht gefunden werden!");
					return;
				}
				if (!friends.contains(t.getUniqueId())) {
					Main.sendMessage(p, "§cDu bist nicht mit diesem Spieler befreundet!");
					return;
				}
				StringBuilder b = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					b.append(args[i]).append(" ");
				}
				reactions.put(t, p);
				Main.sendMessage(p, "§6" + p.getName() + " §7» §6" + t.getName() + "§7: §e" + b.toString());
				Main.sendMessage(t, "§6" + p.getName() + " §7» §6" + t.getName() + "§7: §e" + b.toString());
				return;
			}
			Main.sendMessage(p, "§c/msg <Spieler> <Nachricht>");
		}
	}
}
