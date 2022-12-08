/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.command.commands.bansystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eu.darkcube.system.cloudban.command.Command;
import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.util.ban.BanUtil;
import eu.darkcube.system.cloudban.util.ban.Reason;

public class CommandRemoveReason extends Command {

	public CommandRemoveReason() {
		super(new String[] { "removeReason" }, "removeReason <Reason>",
				"darkcube.bansystem.command.bansystem.removereason", "BanSystem", "Entfernt einen Grund", false);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length == 1) {
			return BanUtil.getReasons().stream().map(Reason::getKey)
					.filter(k -> k.startsWith(args[0])).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length == 1) {
			String key = args[0];
			for (Reason reason : BanUtil.getReasons()) {
				if (reason.getKey().equals(key)) {
					BanUtil.removeReason(reason);
					sender.sendMessage("&aDieser Grund wurde &2erfolgreich &6entfernt!");
					return;
				}
			}
			sender.sendMessage("&cDieser Grund existiert nicht!");
			return;
		}
		sendUsage(sender);
	}
}
