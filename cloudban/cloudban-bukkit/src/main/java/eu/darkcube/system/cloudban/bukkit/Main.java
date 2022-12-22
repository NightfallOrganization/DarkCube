/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.bukkit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.system.cloudban.bukkit.command.*;
import eu.darkcube.system.cloudban.util.Punisher;
import eu.darkcube.system.cloudban.util.Report;
import eu.darkcube.system.cloudban.util.UUIDManager;
import eu.darkcube.system.cloudban.util.ban.Ban;
import eu.darkcube.system.cloudban.util.ban.Server;
import eu.darkcube.system.cloudban.util.communication.EnumChannelMessage;
import eu.darkcube.system.cloudban.util.communication.Messenger;
import eu.darkcube.system.commandapi.CommandAPI;
import eu.darkcube.system.util.ChatUtils;
import eu.darkcube.system.util.ChatUtils.ChatEntry;
import eu.darkcube.system.util.ChatUtils.ChatEntry.Builder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

	private static Main instance;
	private CommandReactReport reactCommand;

	private static Gson gson = new Gson();

	@Override
	public void onEnable() {
		instance = this;
		BukkitCommandWrapper.create();
		getCloudNet().getEventManager().registerListener(this);
		Bukkit.getPluginManager().registerEvents(this, this);

		reactCommand = new CommandReactReport();
		CommandAPI.enable(this, new CommandReport());
		CommandAPI.enable(this, reactCommand);
		CommandAPI.enable(this, new CommandAcceptReport());
		CommandAPI.enable(this, new CommandDeclineReport());
		CommandAPI.enable(this, new CommandRemoveReport());
	}

	@EventListener
	public void handle(ChannelMessageReceiveEvent e) {
		if (e.getChannel() == null || !e.getChannel().equals(Messenger.CHANNEL)) {
			return;
		}
		if (e.getMessage().equals(EnumChannelMessage.UPDATE_USER.getMessage())) {
			UUID uuid = UUID.fromString(e.getData().getString("uuid"));
			Player p = Bukkit.getPlayer(uuid);
			if (p == null) {
				p = Bukkit.getPlayer(UUIDManager.getPlayerName(uuid));
			}
			if (p != null) {
				Punisher.isAllowedToBeOnline(uuid, Server.GLOBAL, new BukkitBanConsumer(p));
			}
		} else if (!e.getChannel().equals(Messenger.CHANNEL)) {
			return;
		}
		if (e.getMessage().equals(EnumChannelMessage.REPORT_SEND_USER_DETAILS.getMessage())) {
			//			Report report = Report.fromDocument(e.getData());

			JsonArray array =
					gson.fromJson(e.getData().toJson(), JsonObject.class).getAsJsonArray("data");
			for (JsonElement element : array) {
				JsonDocument doc = new JsonDocument();
				doc.read(element.toString());
				Report report = Report.fromDocument(doc);
				String uuidString = doc.getString("uuid");
				List<Player> players = new ArrayList<>();
				if ("all".equals(uuidString)) {
					players.addAll(Bukkit.getOnlinePlayers());
				} else {
					UUID uuid = UUID.fromString(uuidString);
					Player p = Bukkit.getPlayer(uuid);
					if (p != null) {
						players.add(p);
					}
				}
				for (Player p : players) {
					if (p.hasPermission(reactCommand.getPermission())) {

						ChatEntry[] headerFooter =
								new Builder().text("§6=====================================")
										.build();
						ChatEntry[] reportMsg1 = new Builder().text(
								"§6Report von §5" + report.getCreator().getName()).build();
						ChatEntry[] reportMsg2 = new Builder().text(
								"§4Verbrecher: §c" + report.getPlayer().getName()).build();
						ChatEntry[] reason =
								new Builder().text("§eGrund: §b" + report.getReason().getDisplay())
										.build();
						List<ChatEntry> entries = new ArrayList<>();
						entries.addAll(Arrays.asList(headerFooter));
						entries.addAll(Arrays.asList(reportMsg1));
						entries.addAll(Arrays.asList(reportMsg2));
						entries.addAll(Arrays.asList(reason));
						entries.addAll(Arrays.asList(
								new Builder().text("§7[§6Bearbeiten§7] ").hover("§6Bearbeiten!")
										.click("/reactreport " + report.getId()).build()));
						entries.addAll(Arrays.asList(
								new Builder().text(" §7[§aAnnehmen§7] ").hover("§aAnnehmen!")
										.click("/acceptreport " + report.getId()).build()));
						entries.addAll(Arrays.asList(
								new Builder().text(" §7[§cAblehnen§7] ").hover("§cAblehnen!")
										.click("/declinereport " + report.getId()).build()));
						entries.addAll(Arrays.asList(
								new Builder().text("§7[§4Entfernen§7] ").hover("§4Entfernen")
										.click("/removereport " + report.getId()).build()));
						entries.addAll(Arrays.asList(headerFooter));

						ChatUtils.chat(entries.toArray(new ChatEntry[0])).send(p);
					}
				}
			}

		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void handle(AsyncPlayerChatEvent e) {
		if (!e.getPlayer().hasPermission(Punisher.PERMISSION_IGNORE_MUTE)) {
			Punisher.isAllowedToChat(e.getPlayer().getUniqueId(), Server.GLOBAL, ban -> {
				e.setCancelled(true);
				e.getPlayer().sendMessage(getMessage(ban));
			});
		}
	}

	private String getMessage(Ban ban) {
		return ChatColor.translateAlternateColorCodes('&',
				Ban.MUTE_MESSAGE.replace("%reason%", ban.getReason().getDisplay())
						.replace("%bannedat%", ban.getTimeBanned().toDate())
						.replace("%timeremain%", ban.getDuration().endingIn(ban.getTimeBanned())));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handle(PlayerLoginEvent e) {
		if (!e.getPlayer().hasPermission(Punisher.PERMISSION_IGNORE_BAN)) {
			Punisher.isAllowedToBeOnline(e.getPlayer().getUniqueId(), Server.GLOBAL,
					new BukkitBanConsumer(e));
		}
	}

	public CommandReactReport getReactCommand() {
		return reactCommand;
	}

	public static final Main getInstance() {
		return instance;
	}

	public static final Wrapper getCloudNet() {
		return Wrapper.getInstance();
	}
}
