/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.bukkit.command;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import de.dytanic.cloudnet.common.document.gson.*;
import de.dytanic.cloudnet.driver.channel.*;
import de.dytanic.cloudnet.ext.bridge.player.*;
import eu.darkcube.system.cloudban.bukkit.*;
import eu.darkcube.system.cloudban.util.*;
import eu.darkcube.system.cloudban.util.communication.*;
import eu.darkcube.system.commandapi.*;
import eu.darkcube.system.commandapi.Command;

public class CommandDeclineReport extends Command {

	public CommandDeclineReport() {
		super(Main.getInstance(), "declinereport", new Command[0], "Decline report",
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
			Report report = Report.byId(id);
			if (report == null) {
				sender.sendMessage("§cUngültige Id. Wahrscheinlich wurde der Report bereits bearbeitet!");
				return true;
			}
			ICloudPlayer online = Util.getManager().getOnlinePlayer(report.getCreator().getUniqueId());
			if (online != null) {
//				Util.getManager().proxySendPlayerMessage(online, "§cDein §5" + report.getReason().getKey() + "§c-Report über §6"
//						+ report.getPlayer().getName() + " §cwurde §4abgelehnt!");
				Util.getManager().getPlayerExecutor(online).sendChatMessage("§cDein §5" + report.getReason().getKey()
						+ "§c-Report über §6" + report.getPlayer().getName() + " §cwurde §4abgelehnt!");
			}
//			Messenger.sendMessage(EnumChannelMessage.REPORT_REMOVE, new JsonDocument().append("id", report.getId()));
			ChannelMessage.builder().channel(Messenger.CHANNEL).message(EnumChannelMessage.REPORT_REMOVE.getMessage())
					.json(new JsonDocument().append("id", report.getId())).build().send();
			sender.sendMessage("§aDu hast den Report über §5" + report.getPlayer().getName() + "§a von §5"
					+ report.getCreator().getName() + " §cabgelehnt!");
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
