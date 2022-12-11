/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.command.commands.bansystem.reason;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.command.SubCommand;
import eu.darkcube.system.cloudban.util.ban.BanUtil;
import eu.darkcube.system.cloudban.util.ban.Reason;

public class CommandRemoveDuration extends SubCommand {

	public CommandRemoveDuration() {
		super(new String[] { "removeDuration" }, "removeDuration <Index | CountBanned>",
				"darkcube.bansystem.command.bansystem.reason.removeduration", "BanSystem",
				"Entfernt die Zeitspanne des angegebenen Indexes");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length == 1) {
			final Reason reason = BanUtil.getReasonByKey(getSpaced());
			if (reason == null)
				return new ArrayList<>();
			return reason.getDurations().keySet().stream().sorted().map(i -> i.toString())
					.filter(i -> i.startsWith(args[0])).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length == 1) {
			final String sReason = getSpaced();
			final Reason reason = BanUtil.getReasonByKey(sReason);
			if (reason == null) {
				sender.sendMessage("&cDieser Grund existiert nicht!");
				return;
			}
			final String sIndex = args[0];
			int index;
			try {
				index = Integer.parseInt(sIndex);
			} catch (Exception ex) {
				sender.sendMessage("&e" + sIndex + " &cist keine Zahl");
				return;
			}
			if (!reason.getDurations().containsKey(index)) {
				sender.sendMessage("&cFür den angegebenen Index existiert keine Zeitspanne");
				return;
			}
			reason.getDurations().remove(index);
			BanUtil.addReason(reason);
			sender.sendMessage("&aZeitspanne &2erfolgreich &eentfernt!");
			return;
		}
		sendUsage(sender);
	}
}
