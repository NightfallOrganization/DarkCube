/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.command.commands.bansystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import eu.darkcube.system.cloudban.command.Command;
import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.command.SubCommand;
import eu.darkcube.system.cloudban.command.commands.bansystem.reason.CommandAddDuration;
import eu.darkcube.system.cloudban.command.commands.bansystem.reason.CommandListDurations;
import eu.darkcube.system.cloudban.command.commands.bansystem.reason.CommandRemoveDuration;
import eu.darkcube.system.cloudban.util.ban.BanUtil;
import eu.darkcube.system.cloudban.util.ban.Reason;

public class CommandReason extends Command {

	private Set<SubCommand> cmds;

	public CommandReason() {
		super(new String[] { "reason" }, "reason <Reason>", "darkcube.bansystem.command.bansystem.reason", "BanSystem",
				"Verwaltung der Gründe", false);
		cmds = new HashSet<>(Arrays.asList(new CommandAddDuration(), new CommandListDurations(), new CommandRemoveDuration()));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length == 2) {
			Set<SubCommand> scmds = cmds.stream()
					.filter(s -> s.getNames()[0].toLowerCase().startsWith(args[1].toLowerCase()))
					.collect(Collectors.toSet());
			if (scmds.size() == 0) {
				return new ArrayList<>();
			}
			for (SubCommand cmd : scmds) {
				cmd.setSpaced(args[0]);
			}
			if (scmds.size() == 1) {
				for (SubCommand cmd : scmds) {
					if (cmd.getNames()[0].equalsIgnoreCase(args[1])) {
						String[] args0 = Arrays.copyOfRange(args, 2, args.length);
						return cmd.onTabComplete(sender, cmd.getNames()[0], args0, commandLine);
					}
					return Arrays.asList(cmd.getNames()[0]);
				}
			} else if (scmds.size() > 1) {
				return scmds.stream().map(Command::getNames).map(s -> s[0]).collect(Collectors.toList());
			}
		} else if (args.length > 2) {
			Optional<SubCommand> ocmd = cmds.stream().filter(s -> s.getNames()[0].equalsIgnoreCase(args[1]))
					.findFirst();
			if (ocmd.isPresent()) {
				String[] args0 = Arrays.copyOfRange(args, 2, args.length);
				return ocmd.get().onTabComplete(sender, ocmd.get().getNames()[0], args0, commandLine);
			}
			return new ArrayList<>();
		} else if (args.length == 1) {
			return BanUtil.getReasons().stream().map(Reason::getKey).filter(s -> s.startsWith(args[0]))
					.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length >= 2) {
			String cmd = args[1];
			String spaced = args[0];
			Optional<SubCommand> ocmd = cmds.stream().filter(s -> s.getNames()[0].equalsIgnoreCase(cmd)).findFirst();
			if (ocmd.isPresent()) {
				String[] args0 = Arrays.copyOfRange(args, 2, args.length);
				ocmd.get().setSpaced(spaced);
				ocmd.get().execute(sender, ocmd.get().getNames()[0], args0, commandLine);
				return;
			}
		}
		for (SubCommand cmd : cmds) {
			sender.sendMessage("&bAliases: &e" + Arrays.toString(cmd.getNames()) + "&7 | &bPermission: &c"
					+ cmd.getPermission() + "&7 - &e" + cmd.getDescription());
		}
	}
}
