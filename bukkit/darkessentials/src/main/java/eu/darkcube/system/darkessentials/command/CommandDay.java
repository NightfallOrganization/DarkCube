/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import eu.darkcube.system.commandapi.*;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.*;
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandDay extends Command {

	public CommandDay() {
		super(DarkEssentials.getInstance(), "day", new Command[0], "Setzt die Zeit auf Tag",
				new Argument("World", "Die Welt in der man Tag setzen will", false));
		setAliases("d_day");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Set<World> worlds = new HashSet<>();
		Set<String> unresolvedNames = new HashSet<>();
		if (args.length > 0) {
			for (String worldName : args) {
				if (Bukkit.getWorld(worldName) != null) {
					worlds.add(Bukkit.getWorld(worldName));
				} else {
					unresolvedNames.add(worldName);
				}
			}
		}
		if (worlds.isEmpty() && unresolvedNames.isEmpty()) {
			if (sender instanceof Player) {
				worlds.add(((Player) sender).getWorld());
			} else {
				DarkEssentials.getInstance().sendMessage(
						DarkEssentials.cFail() + "Du bist kein Spieler, deshalb musst du einen Weltnamen angeben!", sender);
				return true;
			}
		}
		if (!unresolvedNames.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			if (unresolvedNames.size() > 1) {
				sb.append(DarkEssentials.cFail() + "Die Welten " + ChatColor.GRAY + "\"");
			} else {
				sb.append(DarkEssentials.cFail() + "Die Welt " + ChatColor.GRAY + "\"");
			}
			for (String name : unresolvedNames) {
				sb.append(DarkEssentials.cValue() + name + ChatColor.GRAY + "\", \"");
			}
			if (unresolvedNames.size() > 1) {
				DarkEssentials.getInstance().sendMessage(sb.toString().substring(0, sb.toString().length() - 3) + DarkEssentials.cFail()
						+ " wurden nicht gefunden!", sender);
			} else {
				DarkEssentials.getInstance().sendMessage(sb.toString().substring(0, sb.toString().length() - 3) + DarkEssentials.cFail()
						+ " wurde nicht gefunden!", sender);
			}
		}
		int count = 0;
		for (World current : worlds) {
			current.setTime(6000);
			for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
				if (currentPlayer.getWorld().equals(current)) {
					DarkEssentials.getInstance().sendMessage(
							DarkEssentials.cConfirm() + "Die Welt, in der du dich befindest, wurde auf Tag gesetzt.",
							currentPlayer);
				}
			}
			count++;
		}
		Player playerSender = (Player) sender;
		if (!(worlds.size() == 1 && worlds.contains(playerSender.getWorld()) && unresolvedNames.isEmpty())) {
			if (worlds.size() > 1) {
				DarkEssentials.getInstance().sendMessage(
						DarkEssentials.cValue() + String.valueOf(count) + DarkEssentials.cConfirm() + " Welten wurden auf Tag gesetzt.",
						sender);
			} else {
				DarkEssentials.getInstance().sendMessage(
						DarkEssentials.cValue() + count + DarkEssentials.cConfirm() + " Welt wurde auf Tag gesetzt.",
						sender);
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length != 0) {
			return EssentialCollections.toSortedStringList(Bukkit.getWorlds(), EssentialCollections.asList(args),
					args[args.length - 1]);
		}
		return Collections.emptyList();
	}
}
