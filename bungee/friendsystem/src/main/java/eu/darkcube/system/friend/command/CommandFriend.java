/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.friend.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import eu.darkcube.system.friend.Arrays;
import eu.darkcube.system.friend.FriendUtil;
import eu.darkcube.system.friend.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CommandFriend extends Command implements TabExecutor {

	public static IPlayerManager getManager() {
		return CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
	}

	public CommandFriend() {
		super("friend", "bungeecord.command.friend", new String[] {
				"freund"
		});
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer p = (ProxiedPlayer) sender;
		if (args.length >= 1) {
			String cmd = args[0];
			if (cmd.equalsIgnoreCase("list")) {
				Set<UUID> friends = FriendUtil.getFriends(p.getUniqueId());
				Set<ICloudOfflinePlayer> friendsOff = new HashSet<>();
				for (UUID uuid : friends) {
					ICloudOfflinePlayer off = getManager().getOfflinePlayer(uuid);
					if (off != null) {
						friendsOff.add(off);
					}
				}
				if (friendsOff.size() == 0) {
					Main.sendMessage(sender, "§cDu hast keine Freunde §4:(");
				} else {
					Main.sendMessage(sender, "§aDeine Freunde§7:");
					Main.sendMessage(sender, "§8---------------------------");
					for (ICloudOfflinePlayer off : friendsOff) {
						ICloudPlayer on = getManager().getOnlinePlayer(off.getUniqueId());
						Main.sendMessage(sender,
								"§7" + off.getName() + " §7(" + (on != null ? "§aOnline" : "§cOffline") + "§7)");
					}
					Main.sendMessage(sender, "§8---------------------------");
				}
				return;
			} else if (cmd.equalsIgnoreCase("add")) {
				if (args.length == 2) {

					String name = args[1];
					if (name.equals(p.getName())) {
						Main.sendMessage(sender, "§cDu kannst dir nicht selber eine Freundschaftsanfrage schicken!");
						return;
					}
					List<? extends ICloudOfflinePlayer> offlines = getManager().getOfflinePlayers(name);
					if (offlines.size() != 1) {
						Main.sendMessage(sender, "§cSpieler konnte nicht gefunden werden!");
						return;
					}
					ICloudOfflinePlayer offline = offlines.get(0);
					ICloudPlayer online = getManager().getOnlinePlayer(offline.getUniqueId());
					if (FriendUtil.getPending(p.getUniqueId()).contains(offline.getUniqueId())) {
						execute(sender, new String[] {
								"accept", offline.getName()
						});
						return;
					}
					Set<UUID> friends = FriendUtil.getFriends(p.getUniqueId());
					Set<UUID> pending = FriendUtil.getPending(offline.getUniqueId());
					Set<UUID> sent = FriendUtil.getSentRequests(p.getUniqueId());
					if (friends.contains(offline.getUniqueId())) {
						Main.sendMessage(sender, "§cDieser Spieler ist bereits dein Freund!");
						return;
					} else if (pending.contains(p.getUniqueId())) {
						Main.sendMessage(sender,
								"§cDu hast diesem Spieler bereits eine Freundschaftsanfrage geschickt!");
						return;
					}
					if (!FriendUtil.acceptsFriendRequests(offline.getUniqueId())) {
						Main.sendMessage(sender, "§cDieser Spieler empfängt keine Freundschaftsanfragen!");
						return;
					}
					sent.add(offline.getUniqueId());
					pending.add(p.getUniqueId());
					FriendUtil.setSentRequests(p.getUniqueId(), sent);
					FriendUtil.setPending(offline.getUniqueId(), pending);
					if (online != null) {
						TextComponent accept = new TextComponent("§8[§aAnnehmen§8]");
						accept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friend accept " + p.getName()));
						TextComponent deny = new TextComponent("§8[§cAblehnen§8]");
						deny.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friend deny " + p.getName()));
						Main.sendMessage(Main.getInstance().getProxy().getPlayer(offline.getUniqueId()),
								new ComponentBuilder()
										.append("§aDer Spieler §6" + p.getName()
												+ " §ahat dir eine Freundschaftsanfrage geschickt!\n")
										.append(accept)
										.append(" ")
										.append(deny)
										.create());
					}

					Main.sendMessage(sender,
							"§aDu hast dem Spieler §6" + offline.getName() + " §aeine Freundschaftsanfrage geschickt!");
					return;
				}
			} else if (cmd.equalsIgnoreCase("remove")) {
				if (args.length == 2) {
					String name = args[1];
					List<? extends ICloudOfflinePlayer> offlines = getManager().getOfflinePlayers(name);
					if (offlines.size() != 1) {
						Main.sendMessage(sender, "§cSpieler konnte nicht gefunden werden!");
						return;
					}
					ICloudOfflinePlayer offline = offlines.get(0);
					ICloudPlayer online = getManager().getOnlinePlayer(offline.getUniqueId());
					Set<UUID> friends = FriendUtil.getFriends(p.getUniqueId());
					if (!friends.contains(offline.getUniqueId())) {
						Main.sendMessage(sender, "§cDieser Spieler ist nicht dein Freund!");
						return;
					}
					FriendUtil.removeFriends(p.getUniqueId(), offline.getUniqueId());
					FriendUtil.removeFriends(offline.getUniqueId(), p.getUniqueId());
					if (online != null) {
						Main.sendMessage(Main.getInstance().getProxy().getPlayer(online.getUniqueId()),
								"§6" + p.getName() + " §chat dich als Freund entfernt!");
					}
					Main.sendMessage(sender,
							"§cDu hast den Spieler §6" + offline.getName() + " §caus deiner Freundesliste entfernt!");
					return;
				}
			} else if (cmd.equalsIgnoreCase("cancel")) {
				if (args.length == 2) {
					String name = args[1];
					List<? extends ICloudOfflinePlayer> offlines = getManager().getOfflinePlayers(name);
					if (offlines.size() != 1) {
						Main.sendMessage(sender, "§cSpieler konnte nicht gefunden werden!");
						return;
					}
					ICloudOfflinePlayer offline = offlines.get(0);
					ICloudPlayer online = getManager().getOnlinePlayer(offline.getUniqueId());
					Set<UUID> sent = FriendUtil.getSentRequests(p.getUniqueId());
					Set<UUID> pendingOff = FriendUtil.getPending(offline.getUniqueId());
					if (!sent.contains(offline.getUniqueId()) && !pendingOff.contains(p.getUniqueId())) {
						Main.sendMessage(sender, "§cDu hast diesem Spieler keine Freundschaftsanfrage geschickt!");
						return;
					}
					pendingOff.remove(p.getUniqueId());
					sent.remove(offline.getUniqueId());
					FriendUtil.setPending(offline.getUniqueId(), pendingOff);
					FriendUtil.setSentRequests(p.getUniqueId(), sent);
					if (online != null) {
						Main.sendMessage(Main.getInstance().getProxy().getPlayer(online.getUniqueId()),
								"§6" + p.getName() + " §chat seine Freundschaftsanfrage zurückgezogen!");
					}
					Main.sendMessage(sender,
							"§cDu hast die Freundschaftsanfrage an §6" + offline.getName() + " §czurückgezogen!");
					return;
				}
			} else if (cmd.equalsIgnoreCase("accept")) {
				if (args.length == 2) {
					String name = args[1];
					List<? extends ICloudOfflinePlayer> offlines = getManager().getOfflinePlayers(name);
					if (offlines.size() != 1) {
						Main.sendMessage(sender, "§cSpieler konnte nicht gefunden werden!");
						return;
					}
					ICloudOfflinePlayer offline = offlines.get(0);
					ICloudPlayer online = getManager().getOnlinePlayer(offline.getUniqueId());
					Set<UUID> pending = FriendUtil.getPending(p.getUniqueId());
					if (!pending.contains(offline.getUniqueId())) {
						Main.sendMessage(sender, "§cDieser Spieler hat dir keine Freundschaftsanfrage geschickt!");
						return;
					}
					pending.remove(offline.getUniqueId());
					FriendUtil.setPending(p.getUniqueId(), pending);
					FriendUtil.addFriends(p.getUniqueId(), offline.getUniqueId());
					FriendUtil.addFriends(offline.getUniqueId(), p.getUniqueId());
					if (online != null) {
						Main.sendMessage(Main.getInstance().getProxy().getPlayer(online.getUniqueId()),
								"§6" + p.getName() + " §ahat deine Freundschaftsanfrage angenommen!");
					}
					Main.sendMessage(sender,
							"§aDu hast die Freundschaftsanfrage von §6" + offline.getName() + " §aangenommen!");
					return;
				}
			} else if (cmd.equalsIgnoreCase("deny")) {
				if (args.length == 2) {
					String name = args[1];
					List<? extends ICloudOfflinePlayer> offlines = getManager().getOfflinePlayers(name);
					if (offlines.size() != 1) {
						Main.sendMessage(sender, "§cSpieler konnte nicht gefunden werden!");
						return;
					}
					ICloudOfflinePlayer offline = offlines.get(0);
					ICloudPlayer online = getManager().getOnlinePlayer(offline.getUniqueId());
					Set<UUID> pending = FriendUtil.getPending(p.getUniqueId());
					if (!pending.contains(offline.getUniqueId())) {
						Main.sendMessage(sender, "§cDieser Spieler hat dir keine Freundschaftsanfrage geschickt!");
						return;
					}
					pending.remove(offline.getUniqueId());
					FriendUtil.setPending(p.getUniqueId(), pending);
					if (online != null) {
						Main.sendMessage(Main.getInstance().getProxy().getPlayer(online.getUniqueId()),
								"§6" + p.getName() + " §chat deine Freundschaftsanfrage abgelehnt!");
					}
					Main.sendMessage(sender,
							"§cDu hast die Freundschaftsanfrage von §6" + offline.getName() + " §cabgelehnt!");
					return;
				}
			} else if (cmd.equalsIgnoreCase("requests")) {
				Set<UUID> sent = FriendUtil.getSentRequests(p.getUniqueId());
				Set<UUID> pending = FriendUtil.getPending(p.getUniqueId());
				Set<ICloudOfflinePlayer> sentOff = new HashSet<>();
				for (UUID uuid : sent) {
					ICloudOfflinePlayer off = getManager().getOfflinePlayer(uuid);
					if (off != null) {
						sentOff.add(off);
					}
				}
				Set<ICloudOfflinePlayer> pendingOff = new HashSet<>();
				for (UUID uuid : pending) {
					ICloudOfflinePlayer off = getManager().getOfflinePlayer(uuid);
					if (off != null) {
						pendingOff.add(off);
					}
				}
				if (pendingOff.size() == 0) {
					Main.sendMessage(sender, "§cDu hast keine eingehenden Freundschaftsanfragen!");
				} else {
					Main.sendMessage(sender, "§aAnfragen an Dich§7:");
					Main.sendMessage(sender, "§8---------------------------");
					for (ICloudOfflinePlayer off : pendingOff) {
						ICloudPlayer on = getManager().getOnlinePlayer(off.getUniqueId());
						TextComponent base = new TextComponent(
								"§7" + off.getName() + " §7(" + (on != null ? "§aOnline" : "§cOffline") + "§7)");
						TextComponent accept = new TextComponent("§8[§aAnnehmen§8]");
						accept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friend accept " + off.getName()));
						accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aAnnehmen")));
						TextComponent space = new TextComponent(" ");
						TextComponent deny = new TextComponent("§8[§cAblehnen§8]");
						deny.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friend deny " + off.getName()));
						deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§cAblehnen")));
						Main.sendMessage(sender, base, space, accept, space, deny);
					}
					Main.sendMessage(sender, "§8---------------------------");
				}
				if (sentOff.size() == 0) {
					Main.sendMessage(sender, "§cDu hast keine ausgehenden Freundschaftsanfragen!");
				} else {
					Main.sendMessage(sender, "§aAnfragen von Dir§7:");
					Main.sendMessage(sender, "§8---------------------------");
					for (ICloudOfflinePlayer off : sentOff) {
						ICloudPlayer on = getManager().getOnlinePlayer(off.getUniqueId());
						TextComponent base = new TextComponent(
								"§7" + off.getName() + " §7(" + (on != null ? "§aOnline" : "§cOffline") + "§7)");
						TextComponent retrieve = new TextComponent("§8[§6Zurückziehen§8]");
						retrieve.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friend cancel " + off.getName()));
						retrieve.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Zurückziehen")));
						TextComponent space = new TextComponent(" ");
						Main.sendMessage(sender, base, space, retrieve);
					}
					Main.sendMessage(sender, "§8---------------------------");
				}
				return;
			} else if (cmd.equalsIgnoreCase("jump")) {
				if (args.length == 2) {
					String name = args[1];
					List<? extends ICloudOfflinePlayer> offlines = getManager().getOfflinePlayers(name);
					if (offlines.size() != 1) {
						Main.sendMessage(sender, "§cSpieler konnte nicht gefunden werden!");
						return;
					}
					ICloudOfflinePlayer offline = offlines.get(0);
					if (!FriendUtil.getFriends(p.getUniqueId()).contains(offline.getUniqueId())) {
						Main.sendMessage(p, "§cDu bist nicht mit diesem Spieler befreundet!");
						return;
					}
					ICloudPlayer online = getManager().getOnlinePlayer(offline.getUniqueId());
					if (online == null) {
						Main.sendMessage(p, "§cDieser Spieler ist nicht online!");
						return;
					}
					ProxiedPlayer t = Main.getInstance().getProxy().getPlayer(online.getUniqueId());
					if (!t.getServer().getInfo().getName().equals(p.getServer().getInfo().getName())) {
						p.connect(t.getServer().getInfo());
						Main.sendMessage(t, "§6" + p.getName() + " §eist zu dir gesprungen!");
					}
					return;
				}
			} else if (cmd.equalsIgnoreCase("toggle")) {
				FriendUtil.setAcceptsFriendRequests(p.getUniqueId(),
						!FriendUtil.acceptsFriendRequests(p.getUniqueId()));
				if (FriendUtil.acceptsFriendRequests(p.getUniqueId())) {
					Main.sendMessage(sender, "§aDu akzeptierst nun Freundschaftsanfragen!");
				} else {
					Main.sendMessage(sender, "§cDu akzeptierst nun keine Freundschaftsanfragen mehr!");
				}
				return;
			}
		}
		Main.sendMessage(sender, "§c/friend list §8| §6Liste deine Freunde auf");
		Main.sendMessage(sender, "§c/friend add <Spieler> §8| §6Füge einen Spieler als Freund hinzu");
		Main.sendMessage(sender, "§c/friend remove <Spieler> §8| §6Entferne einen Freund");
		Main.sendMessage(sender, "§c/friend cancel <Spieler> §8| §6Ziehe gesendete Freundschaftsanfragen zurück");
		Main.sendMessage(sender, "§c/friend accept <Spieler> §8| §6Nimm eine Freundschaftsanfrage an");
		Main.sendMessage(sender, "§c/friend deny <Spieler> §8| §6Lehne eine Freundschaftsanfrage ab");
		Main.sendMessage(sender, "§c/friend requests §8| §6Liste Freundschaftsanfragen auf");
		Main.sendMessage(sender, "§c/friend jump <Spieler> §8| §6Springe zu einem Spieler");
		Main.sendMessage(sender, "§c/friend toggle §8| §6Schalte Freundschaftsanfragen aus/ein");
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return new ArrayList<>();
		}
		ProxiedPlayer p = (ProxiedPlayer) sender;
		if (args.length == 1) {
			return Arrays.toSortedStringList(new String[] {
					"list", "add", "remove", "cancel", "accept", "deny", "requests", "jump", "toggle"
			}, args[0]);
		} else if (args.length == 2) {
			String cmd = args[0];
			if (cmd.equalsIgnoreCase("add")) {
				List<String> list = new ArrayList<>();
				Set<UUID> friends = FriendUtil.getFriends(p.getUniqueId());
				for (ProxiedPlayer player : Main.getInstance().getProxy().getPlayers()) {
					if (!friends.contains(player.getUniqueId())) {
						if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
							Set<UUID> pending = FriendUtil.getPending(player.getUniqueId());
							if (!pending.contains(p.getUniqueId())) {
								list.add(player.getName());
							}
						}
					}
				}
				return list;
			} else if (cmd.equalsIgnoreCase("remove")) {
				List<String> list = new ArrayList<>();
				Set<UUID> friends = FriendUtil.getFriends(p.getUniqueId());
				for (UUID uuid : friends) {
					ICloudOfflinePlayer player = getManager().getOfflinePlayer(uuid);
					if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
						list.add(player.getName());
					}
				}
				return list;
			} else if (cmd.equalsIgnoreCase("cancel")) {
				List<String> list = new ArrayList<>();
				Set<UUID> pending = FriendUtil.getSentRequests(p.getUniqueId());
				for (UUID uuid : pending) {
					ICloudOfflinePlayer player = getManager().getOfflinePlayer(uuid);
					if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
						list.add(player.getName());
					}
				}
				return list;
			} else if (cmd.equalsIgnoreCase("deny")) {
				List<String> list = new ArrayList<>();
				Set<UUID> pending = FriendUtil.getPending(p.getUniqueId());
				for (UUID uuid : pending) {
					ICloudOfflinePlayer player = getManager().getOfflinePlayer(uuid);
					if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
						list.add(player.getName());
					}
				}
				return list;
			} else if (cmd.equalsIgnoreCase("accept")) {
				List<String> list = new ArrayList<>();
				Set<UUID> pending = FriendUtil.getPending(p.getUniqueId());
				for (UUID uuid : pending) {
					ICloudOfflinePlayer player = getManager().getOfflinePlayer(uuid);
					if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
						list.add(player.getName());
					}
				}
				return list;
			} else if (cmd.equalsIgnoreCase("jump")) {
				List<String> list = new ArrayList<>();
				Set<UUID> friends = FriendUtil.getFriends(p.getUniqueId());
				for (ProxiedPlayer player : Main.getInstance().getProxy().getPlayers()) {
					if (friends.contains(player.getUniqueId())) {
						if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
							list.add(player.getName());
						}
					}
				}
				return list;
			}
		}
		return new ArrayList<>();
	}
}
