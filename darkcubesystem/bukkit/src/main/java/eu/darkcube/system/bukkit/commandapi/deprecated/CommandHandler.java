/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.deprecated;

import eu.darkcube.system.bukkit.commandapi.deprecated.SpacedCommand.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandHandler implements TabExecutor {

	private CommandAPI instance;

	public CommandHandler(CommandAPI instance) {
		this.instance = instance;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd,
			String label, String[] args) {
		Command CMD = instance.main_command;
		System.out.println("Tab complete");
		if (!CMD.hasPermission(sender))
			return Collections.emptyList();
		Command oldCMD = CMD;
		boolean cancel = false;
		int level = 0;
		String lastArg = args[level];

		while (!cancel && CMD.hasSubCommands() && level < args.length) {
			lastArg = args[level];
			Command c = Command.getCommand(CMD.getChilds(), lastArg);
			if (c == null) {
				cancel = true;
			} else {
				oldCMD = CMD;
				CMD = c;
				if (c instanceof ISpaced && args.length > level + 1) {
					((ISpaced) c).setSpaced(args[++level]);
					lastArg = args[level];
				}
			}
			level++;
		}

		String[] lastArgs = new String[args.length - level];
		for (int i = 0; level < args.length; level++, i++)
			lastArgs[i] = args[level];

		if (CMD.onTabComplete(lastArgs) == null) {
			List<String> players = new ArrayList<>();
			for (Player p : Bukkit.getOnlinePlayers())
				players.add(p.getName());
			return players;
		}

		if (!CMD.onTabComplete(lastArgs).isEmpty()) {
			return CMD.onTabComplete(lastArgs);
		}

		if (CMD instanceof ISpaced || CMD instanceof SubCommand) {
		} else if ((level - 1) != CMD.getPosition().getPosition()) {
			CMD = oldCMD;
		}

		if (cancel) {
			if (CMD.onTabComplete(lastArgs).isEmpty()) {
				return Command.vv(sender, CMD.getChilds(), lastArg);
			}
			return CMD.onTabComplete(lastArgs);
		}

		if (lastArgs.length != 0)
			return CMD.onTabComplete(lastArgs);

		if (CMD instanceof ISpaced) {
			return CMD.onTabComplete(new String[] {lastArg});
		}

		return Command.vv(sender, CMD.getChilds(), lastArg);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label,
			String[] args) {

		Command CMD = instance.main_command;

		if (args.length == 0 && CMD.hasSubCommands()) {
			CMD.sendUsage(sender);
			return true;
		} else if (args.length == 0) {
			CMD.onCommand(sender, args, "");
			return true;
		}

		boolean cancel = false;
		int level = 0;
		String lastArg = args[level];

		while (!cancel && CMD.hasSubCommands() && level < args.length) {
			lastArg = args[level];
			Command c = Command.getCommand(CMD.getChilds(), lastArg);
			if (c == null) {
				cancel = true;
			} else {
				CMD = c;
				if (c instanceof ISpaced && args.length > level + 1) {
					((ISpaced) c).setSpaced(args[++level]);
				}
			}
			level++;
		}

		if (cancel) {
			CMD.sendUsage(sender);
			return true;
		}

		String[] lastArgs = new String[args.length - level];
		for (int i = 0; level < args.length; level++, i++)
			lastArgs[i] = args[level];

		if (lastArg.equalsIgnoreCase(CMD.getName()))
			lastArg = "";

		CMD.onCommand(sender, lastArgs, lastArg);
		return true;
	}
}
