/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
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

public class CommandNight extends Command {

	public CommandNight() {
		super(DarkEssentials.getInstance(), "night", new Command[0], "Setzt die Zeit auf Nacht",
				new Argument("World", "Die Welt in der man Nacht setzen will", false));
		setAliases("d_night");
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
				DarkEssentials.getPlugin(DarkEssentials.class)
						.sendMessage("§cDu bist kein Spieler, deshalb musst du einen Weltnamen angeben!", sender);
				return true;
			}
		}
		if (unresolvedNames.size() != 0) {
			StringBuilder sb = new StringBuilder().append(
					DarkEssentials.cFail() + "Die Welt " + ChatColor.GRAY + "\"");
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
			current.setTime(18000);
			for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
				if (currentPlayer.getWorld().equals(current)) {
					DarkEssentials.getInstance().sendMessage(
							DarkEssentials.cConfirm() + "Die Welt, in der du dich befindest, wurde auf Nacht gesetzt.",
							currentPlayer);
				}
			}
			count++;
		}
		Player playerSender = (Player) sender;
		if (!(worlds.size() == 1 && worlds.contains(playerSender.getWorld()) && unresolvedNames.isEmpty())) {
			if (worlds.size() > 1) {
				DarkEssentials.getInstance().sendMessage(
						DarkEssentials.cValue() + count + DarkEssentials.cConfirm() + " Welten wurden auf Nacht gesetzt.",
						sender);
			} else {
				DarkEssentials.getInstance().sendMessage(
						DarkEssentials.cValue() + count + DarkEssentials.cConfirm() + " Welt wurde auf Nacht gesetzt.",
						sender);
			}
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length != 0) {
			return EssentialCollections.toSortedStringList(Bukkit.getWorlds(), EssentialCollections.asList(args),
					args[args.length - 1]);
		}
		return list;
	}
}
