/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.bukkit;

import java.util.*;
import java.util.Arrays;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.*;

import com.google.gson.*;

import de.dytanic.cloudnet.common.document.gson.*;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.*;
import de.dytanic.cloudnet.wrapper.*;
import eu.darkcube.system.*;
import eu.darkcube.system.ChatUtils.*;
import eu.darkcube.system.ChatUtils.ChatEntry.*;
import eu.darkcube.system.cloudban.bukkit.command.*;
import eu.darkcube.system.cloudban.util.*;
import eu.darkcube.system.cloudban.util.ban.*;
import eu.darkcube.system.cloudban.util.ban.Server;
import eu.darkcube.system.cloudban.util.communication.*;
import eu.darkcube.system.commandapi.*;

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

			JsonArray array = gson.fromJson(e.getData().toJson(), JsonObject.class).getAsJsonArray("data");
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

						ChatEntry[] headerFooter = new Builder().text("§6=====================================").build();
						ChatEntry[] reportMsg1 = new Builder().text("§6Report von §5" + report.getCreator().getName()).build();
						ChatEntry[] reportMsg2 = new Builder().text("§4Verbrecher: §c" + report.getPlayer().getName()).build();
						ChatEntry[] reason = new Builder().text("§eGrund: §b" + report.getReason().getDisplay()).build();
						List<ChatEntry> entries = new ArrayList<>();
						entries.addAll(Arrays.asList(headerFooter));
						entries.addAll(Arrays.asList(reportMsg1));
						entries.addAll(Arrays.asList(reportMsg2));
						entries.addAll(Arrays.asList(reason));
						entries.addAll(Arrays.asList(new Builder().text("§7[§6Bearbeiten§7] ").hover("§6Bearbeiten!")
								.click("/reactreport " + report.getId()).build()));
						entries.addAll(Arrays.asList(new Builder().text(" §7[§aAnnehmen§7] ").hover("§aAnnehmen!")
								.click("/acceptreport " + report.getId()).build()));
						entries.addAll(Arrays.asList(new Builder().text(" §7[§cAblehnen§7] ").hover("§cAblehnen!")
								.click("/declinereport " + report.getId()).build()));
						entries.addAll(Arrays.asList(new Builder().text("§7[§4Entfernen§7] ").hover("§4Entfernen")
								.click("/removereport " + report.getId()).build()));
						entries.addAll(Arrays.asList(headerFooter));
						
						ChatBaseComponent c = ChatUtils.chat(entries.toArray(new ChatEntry[0]));
						c.sendPlayer(p);
//						p.getHandle().sendMessage(new IChatBaseComponent[] { headerFooter, reportMsg1, reportMsg2,
//								reason, reportMsg3, headerFooter });
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
			Punisher.isAllowedToBeOnline(e.getPlayer().getUniqueId(), Server.GLOBAL, new BukkitBanConsumer(e));
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
