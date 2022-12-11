/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.StatsPlugin;
import eu.darkcube.system.stats.api.user.StatsUserManager;
import eu.darkcube.system.stats.api.user.User;

public class CommandStats extends Command {

	public CommandStats() {
		super(StatsPlugin.getInstance(), "stats", new Command[0], "Zeigt die Statistiken eines Spielers an!",
				new Argument("Spieler", "Der Spieler"), new Argument("Duration", "Die Zeitspanne", false));
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return StatsUserManager.getOnlineUsers().stream().map(p -> p.getName()).filter(n -> n.startsWith(args[0]))
					.collect(Collectors.toList());
		} else if (args.length == 2) {
			return Arrays.asList(Duration.values()).stream().map(Duration::format)
					.filter(d -> d.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
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
			args = new String[] { sender.getName() };
		}
		if (args.length == 1 || args.length == 2) {
			Duration duration = Duration.ALLTIME;
			if (args.length == 2) {
				for (Duration d : Duration.values()) {
					if (d.format().equalsIgnoreCase(args[1])) {
						duration = d;
						break;
					}
				}
			}
			String c = "";
			switch (duration) {
			case ALLTIME:
				c = "/astats " + args[0];
				break;
			case DAY:
				c = "/dstats " + args[0];
				break;
			case HOUR:
				c = "/hstats " + args[0];
				break;
			case MONTH:
				c = "/mstats " + args[0];
				break;
			case WEEK:
				c = "/wstats " + args[0];
				break;
			case YEAR:
				c = "/ystats " + args[0];
				break;
			}
			((Player) sender).chat(c);
			return true;
		}
		sender.sendMessage(getSimpleLongUsage());
		return true;
	}

	static User doPlayerStuff(CommandSender sender, String name) {
		List<User> s = StatsUserManager.getOfflineUser(name);
		if (s.size() == 0) {
			sender.sendMessage("§cSpieler konnte nicht gefunden werden!");
			return null;
		}
		return s.get(0);
	}
}
