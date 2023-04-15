/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandTimefreeze extends Command {

	public CommandTimefreeze() {
		super(DarkEssentials.getInstance(), "timefreeze", new Command[0],
				"Setzt die Zeit auf einen bestimmten Wert und stoppt den Tagesverlauf.",
				new Argument("day|noon|night|Wert|disable",
						"Die Zeit, die gesetzt werden soll (Disable: Zeit normal weiter laufen lassen)."),
				new Argument("Welt", "Die Welt, in der die Zeit gesetzt wird (\"all\" für alle Welten.)", false));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		int timeToSet = 0;
		switch (args[0].toLowerCase()) {
		case "day":
			timeToSet = 0;
			break;
		case "noon":
			timeToSet = 6000;
			break;
		case "night":
			timeToSet = 13000;
			break;
		case "disable":
			Bukkit.getWorlds().forEach(w -> w.setGameRuleValue("doDaylightCycle", "true"));
		default:
			try {
				timeToSet = Integer.parseInt(args[0]);
			} catch (Exception e) {
				DarkEssentials.getInstance().sendMessage(
						new StringBuilder(DarkEssentials.cFail()).append("Du musst eine Zeit angeben!").toString(), sender);
			}
		}
		args[0] = "%processed%";
		Set<String> unresolvedNames = new HashSet<>();
		Set<World> worlds = new HashSet<>();
		for (String worldName : args) {
			if (worldName.equals("all")) {
				worlds.addAll(Bukkit.getWorlds());
			} else if (Bukkit.getWorld(worldName) != null) {
				worlds.add(Bukkit.getWorld(worldName));
			} else if (worldName.equals("%processed%")) {
				unresolvedNames.add(worldName);
			}
		}
		if (worlds.isEmpty() && unresolvedNames.isEmpty()) {
			if (sender instanceof Player) {
				worlds.add(((Player) sender).getWorld());
			} else {
				DarkEssentials.getInstance().sendMessage(
						DarkEssentials.cFail() + "Du bist kein Spieler, deshalb musst du eine Welt angeben!", sender);
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
			current.setTime(timeToSet);
			current.setGameRuleValue("doDaylightCycle", "false");
			StringBuilder sb = new StringBuilder(DarkEssentials.cConfirm())
					.append("Die Welt, in der du dich befindest, wurde auf ").append(DarkEssentials.cValue()).append(timeToSet)
					.append(DarkEssentials.cConfirm()).append(" gesetzt.");
			for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
				if (currentPlayer.getWorld().equals(current)) {
					DarkEssentials.getInstance().sendMessage(sb.toString(), currentPlayer);

				}
			}
			count++;
		}

		if (!(worlds.size() == 1 && worlds.contains(((Player) sender).getWorld()) && unresolvedNames.isEmpty())) {
			if (worlds.size() > 1) {

				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder().append(DarkEssentials.cValue()).append(count).append(
										DarkEssentials.cConfirm())
								.append(" Welten auf ").append(DarkEssentials.cValue()).append(timeToSet).append(
										DarkEssentials.cConfirm())
								.append(" gesetzt.").toString(), sender);
			} else {
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder().append(DarkEssentials.cValue()).append(count).append(
										DarkEssentials.cConfirm())
								.append(" Welt auf ").append(DarkEssentials.cValue()).append(timeToSet).append(
										DarkEssentials.cConfirm())
								.append(" gesetzt.").toString(), sender);
			}
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1)
			return EssentialCollections.toSortedStringList(Arrays.asList("day", "noon", "night", "disable"), args[0]);
		if (args.length > 1)
			return EssentialCollections.toSortedStringList(Bukkit.getWorlds(), args[args.length - 1]);
		return new ArrayList<>();
	}

}
