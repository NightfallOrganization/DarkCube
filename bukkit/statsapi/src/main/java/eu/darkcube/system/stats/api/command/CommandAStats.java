/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.command;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.StatsPlugin;
import eu.darkcube.system.stats.api.stats.Stats;
import eu.darkcube.system.stats.api.user.StatsUserManager;
import eu.darkcube.system.stats.api.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandAStats extends Command {

	public CommandAStats() {
		super(StatsPlugin.getInstance(), "astats", new Command[0],
				"Zeigt die Alltime Statistiken eines Spielers an",
				new Argument("Spieler", "Der Spieler"));
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return StatsUserManager.getOnlineUsers().stream().map(p -> p.getName())
					.filter(n -> n.startsWith(args[0])).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You are not a player!");
			return true;
		}
		if (args.length == 0) {
			args = new String[] {sender.getName()};
		}
		if (args.length == 1) {
			User user = CommandStats.doPlayerStuff(sender, args[0]);
			if (user != null) {
				Stats stats = user.getLastStats(Duration.ALLTIME);
				stats.formatComponent().send(sender);
			}
			return true;
		}
		sender.sendMessage(getSimpleLongUsage());
		return true;
	}
}
