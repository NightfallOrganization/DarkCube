/*
 * Copyright (c) 2022. [DarkCube]
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
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandTimefreeze extends Command {

	public CommandTimefreeze() {
		super(Main.getInstance(), "timefreeze", new Command[0],
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
				Main.getInstance().sendMessage(
						new StringBuilder(Main.cFail()).append("Du musst eine Zeit angeben!").toString(), sender);
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
				Main.getInstance().sendMessage(
						Main.cFail() + "Du bist kein Spieler, deshalb musst du eine Welt angeben!", sender);
				return true;
			}
		}
		if (!unresolvedNames.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			if (unresolvedNames.size() > 1) {
				sb.append(Main.cFail() + "Die Welten " + ChatColor.GRAY + "\"");
			} else {
				sb.append(Main.cFail() + "Die Welt " + ChatColor.GRAY + "\"");
			}
			for (String name : unresolvedNames) {
				sb.append(Main.cValue() + name + ChatColor.GRAY + "\", \"");
			}
			if (unresolvedNames.size() > 1) {
				Main.getInstance().sendMessage(sb.toString().substring(0, sb.toString().length() - 3) + Main.cFail()
						+ " wurden nicht gefunden!", sender);
			} else {
				Main.getInstance().sendMessage(sb.toString().substring(0, sb.toString().length() - 3) + Main.cFail()
						+ " wurde nicht gefunden!", sender);
			}
		}
		int count = 0;
		for (World current : worlds) {
			current.setTime(timeToSet);
			current.setGameRuleValue("doDaylightCycle", "false");
			StringBuilder sb = new StringBuilder(Main.cConfirm())
					.append("Die Welt, in der du dich befindest, wurde auf ").append(Main.cValue()).append(timeToSet)
					.append(Main.cConfirm()).append(" gesetzt.");
			for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
				if (currentPlayer.getWorld().equals(current)) {
					Main.getInstance().sendMessage(sb.toString(), currentPlayer);

				}
			}
			count++;
		}

		if (!(worlds.size() == 1 && worlds.contains(((Player) sender).getWorld()) && unresolvedNames.isEmpty())) {
			if (worlds.size() > 1) {

				Main.getInstance()
						.sendMessage(new StringBuilder().append(Main.cValue()).append(count).append(Main.cConfirm())
								.append(" Welten auf ").append(Main.cValue()).append(timeToSet).append(Main.cConfirm())
								.append(" gesetzt.").toString(), sender);
			} else {
				Main.getInstance()
						.sendMessage(new StringBuilder().append(Main.cValue()).append(count).append(Main.cConfirm())
								.append(" Welt auf ").append(Main.cValue()).append(timeToSet).append(Main.cConfirm())
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
