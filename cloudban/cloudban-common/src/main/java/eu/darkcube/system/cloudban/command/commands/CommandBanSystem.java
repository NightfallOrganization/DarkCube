/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import eu.darkcube.system.cloudban.command.Command;
import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.command.commands.bansystem.CommandAddReason;
import eu.darkcube.system.cloudban.command.commands.bansystem.CommandListReasons;
import eu.darkcube.system.cloudban.command.commands.bansystem.CommandReason;
import eu.darkcube.system.cloudban.command.commands.bansystem.CommandRemoveReason;

public class CommandBanSystem extends Command {

	private CommandAddReason addReason;
	private CommandListReasons listReasons;
	private CommandRemoveReason removeReason;
	private CommandReason reason;
	private List<Command> cmds;

	public CommandBanSystem() {
		super(new String[] { "bansystem", "bsystem" }, "/bansystem <SubCommand>",
				"darkcube.bansystem.command.bansystem", "BanSystem", "Verwaltung des BanSystems");
		addReason = new CommandAddReason();
		listReasons = new CommandListReasons();
		removeReason = new CommandRemoveReason();
		reason = new CommandReason();
		cmds = Arrays.asList(addReason, listReasons, removeReason, reason);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length == 1) {
			return cmds.stream().map(Command::getNames).map(a -> a[0])
					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}
		String cmd = args[0];
		Optional<Command> ocmd = cmds.stream().filter(s -> s.getNames()[0].equalsIgnoreCase(cmd)).findFirst();
		if (ocmd.isPresent()) {
			String[] args0 = Arrays.copyOfRange(args, 1, args.length);
			return ocmd.get().onTabComplete(sender, ocmd.get().getNames()[0], args0, commandLine);
		}
		return new ArrayList<>();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length >= 1) {
			String cmd = args[0];
			Optional<Command> ocmd = cmds.stream().filter(s -> s.getNames()[0].equalsIgnoreCase(cmd)).findFirst();
			if (ocmd.isPresent()) {
				String[] args0 = Arrays.copyOfRange(args, 1, args.length);
				ocmd.get().execute(sender, ocmd.get().getNames()[0], args0, commandLine);
				return;
			}
		}
		for (Command cmd : cmds) {
			sender.sendMessage("&bAliases: &e" + Arrays.toString(cmd.getNames()) + "&7 | &bPermission: &c"
					+ cmd.getPermission() + "&7 - &e" + cmd.getDescription());
		}
	}
}
