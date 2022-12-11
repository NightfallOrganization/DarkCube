/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.command.commands.bansystem.reason;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.command.SubCommand;
import eu.darkcube.system.cloudban.util.ban.BanDuration;
import eu.darkcube.system.cloudban.util.ban.BanType;
import eu.darkcube.system.cloudban.util.ban.BanUtil;
import eu.darkcube.system.cloudban.util.ban.Duration;
import eu.darkcube.system.cloudban.util.ban.DurationDeserializer;
import eu.darkcube.system.cloudban.util.ban.Reason;

public class CommandAddDuration extends SubCommand {

	public CommandAddDuration() {
		super(new String[] { "addDuration" }, "addDuration <CountBanned> <Duration> <BanType> [<Override>]",
				"darkcube.bansystem.command.bansystem.reason.addduration", "BanSystem",
				"Setzt eine Zeitspanne für den angegebenen Index");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length == 1) {
			final Reason reason = BanUtil.getReasonByKey(getSpaced());
			if (reason == null)
				return new ArrayList<>();
			return Arrays.asList(
					Integer.toString(reason.getDurations().keySet().stream().mapToInt(i -> i).max().orElse(0) + 1));
		} else if (args.length == 3) {
			return Arrays.asList(BanType.values()).stream().map(BanType::name)
					.filter(i -> i.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
		} else if (args.length == 4) {
			return Arrays.asList("true");
		}
		return new ArrayList<>();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length >= 3) {
			final String sReason = getSpaced();
			final String sIndex = args[0];
			final String sDuration = args[1];
			final String sType = args[2];
			final Reason reason = BanUtil.getReasonByKey(sReason);
			boolean override = false;
			if (args.length > 3 && args[3].equalsIgnoreCase("true")) {
				override = true;
			}
			if (reason == null) {
				sender.sendMessage("&cDieser Grund existiert nicht!");
				return;
			}
			int index;
			try {
				index = Integer.parseInt(sIndex);
				if (index < 1)
					throw new Exception();
			} catch (Exception ex) {
				sender.sendMessage("&e" + sIndex + " &cist keine gültige Zahl");
				return;
			}
			if (!override && reason.getDurations().get(index) != null) {
				sender.sendMessage(
						"&cFür diesen Index wurde bereits eine Zeitspanne gesetzt. Zum überschreiben den [<Override>] parameter auf 'true' setzen.");
				return;
			}
			Duration duration = DurationDeserializer.deserialize(sDuration);
			if (duration == null || duration.equals(Duration.WARNING)) {
				sender.sendMessage("&e" + sDuration + " &cist keine gültige Zeitspanne!");
				return;
			}
			BanType type = BanType.valueOf(sType);
			if (type == null) {
				sender.sendMessage("&cDiser Ban-Typ existiert nicht!");
				return;
			}
			reason.getDurations().put(index, new BanDuration(duration.getDurationInSeconds(), type));
			BanUtil.addReason(reason);
			sender.sendMessage("&aZeitraum hinzugefügt!");
			return;
		}
		sendUsage(sender);
	}
}
