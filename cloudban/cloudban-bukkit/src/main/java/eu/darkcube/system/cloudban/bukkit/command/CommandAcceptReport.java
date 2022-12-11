/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.bukkit.command;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import eu.darkcube.system.cloudban.bukkit.Main;
import eu.darkcube.system.cloudban.util.Punisher;
import eu.darkcube.system.cloudban.util.Punisher.PunishmentData;
import eu.darkcube.system.cloudban.util.Report;
import eu.darkcube.system.cloudban.util.ban.Ban;
import eu.darkcube.system.cloudban.util.ban.BanUtil;
import eu.darkcube.system.cloudban.util.ban.Server;
import eu.darkcube.system.cloudban.util.communication.EnumChannelMessage;
import eu.darkcube.system.cloudban.util.communication.Messenger;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;

public class CommandAcceptReport extends Command {

	public CommandAcceptReport() {
		super(Main.getInstance(), "acceptreport", new Command[0], "Accept report",
				new Argument("ReportId", "ReportId"));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				return false;
			}
			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (Exception ex) {
				sender.sendMessage("§cUngültige Id. Wahrscheinlich wurde der Report bereits bearbeitet!");
				return true;
			}
			UUID uuid = ((Player) sender).getUniqueId();
			Report report = Report.byId(id);
			if (report == null) {
				sender.sendMessage("§cUngültige Id. Wahrscheinlich wurde der Report bereits bearbeitet!");
				return true;
			}
//			Messenger.sendMessage(EnumChannelMessage.REPORT_REMOVE, new JsonDocument().append("id", report.getId()));
			ChannelMessage.builder().channel(Messenger.CHANNEL).message(EnumChannelMessage.REPORT_REMOVE.getMessage())
					.json(new JsonDocument().append("id", report.getId())).build().send();

			Set<Ban> bans = new HashSet<>();
			bans.addAll(BanUtil.getUserInformation(uuid).getBans());

			Ban ban = bans.stream()
					.filter(b -> b.getReason().equals(report.getReason()) && b.getServer().equals(Server.GLOBAL))
					.findFirst().orElse(null);

			if (ban == null) {
				PunishmentData data = Punisher.punish(report.getPlayer().getUniqueId(), report.getPlayer().getName(),
						report.getReason(), ((Player) sender).getUniqueId(), sender.getName(), Server.GLOBAL);

				if (data.isHistorySuccess()) {
					sender.sendMessage("§aDer Ban wurde in den Ban-Logs des Spielers gespeichert!");
				} else {
					sender.sendMessage("§cDer Ban konnte nicht in den Ban-Logs des Spielers gespeichert werden!");
				}
				if (data.isInformationSuccess()) {
					sender.sendMessage("§aDer Spieler wurde für §e" + report.getReason().getDisplay() + " §cgebannt!");
				} else {
					sender.sendMessage("§cDer Spieler konnte nicht gebannt werden!");
				}
			}
			sender.sendMessage("§aReport §2angenommen!");
//			ICloudPlayer target = pm.getOnlinePlayer(report.getPlayer().getUniqueId());
//			if (target == null) {
//				sender.sendMessage("§cDieser Spieler ist nicht online!");
//				return true;
//			}
//			pm.proxySendPlayer(pm.getOnlinePlayer(((Player) sender).getUniqueId()),
//					target.getConnectedService().getServerName());
			return true;
		}
		sender.sendMessage(getSimpleLongUsage());
		return true;
	}
}
