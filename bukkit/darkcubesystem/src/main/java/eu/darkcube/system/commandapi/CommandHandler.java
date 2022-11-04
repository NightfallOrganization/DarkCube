package eu.darkcube.system.commandapi;

import java.util.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import eu.darkcube.system.commandapi.SpacedCommand.*;

public class CommandHandler implements TabExecutor {

	private CommandAPI instance;

	public CommandHandler(CommandAPI instance) {
		this.instance = instance;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label,
			String[] args) {
		
		Command CMD = instance.main_command;
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
			return CMD.onTabComplete(new String[] { lastArg });
		}

		return Command.vv(sender, CMD.getChilds(), lastArg);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {

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