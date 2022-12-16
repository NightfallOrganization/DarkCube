/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.command.commands.bansystem.reason;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.command.SubCommand;
import eu.darkcube.system.cloudban.util.ban.BanUtil;
import eu.darkcube.system.cloudban.util.ban.Reason;

public class CommandListDurations extends SubCommand {

	public CommandListDurations() {
		super(new String[] { "listDurations" }, "listDurations",
				"darkcube.bansystem.command.bansystem.reason.listdurations", "BanSystem",
				"Listet alle Zeitspannen eines Ban-Grundes auf");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] args, String commandLine) {
		return new ArrayList<>();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args, String commandLine) {
		String sReason = getSpaced();
		Reason reason = BanUtil.getReasonByKey(sReason);
		if (reason == null) {
			sender.sendMessage("&cDieser Grund existiert nicht!");
			return;
		}
		if (reason.getDurations().size() == 0) {
			sender.sendMessage("&cFür diesen Ban-Grund gibt es keine Zeitspannen!");
			return;
		}
		sender.sendMessage("&aZeitspannen für Grund &e" + reason.getKey());
		for (int index : reason.getDurations().keySet()) {
			sender.sendMessage("&b" + index + "&7 | &6" + reason.getDurations().get(index).getType() + "&7 | &e"
					+ reason.getDurations().get(index).toText());
		}
	}
}
