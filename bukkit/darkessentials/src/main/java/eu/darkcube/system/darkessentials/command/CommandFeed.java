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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandFeed extends Command {

	public CommandFeed() {
		super(Main.getInstance(), "feed", new Command[0], "Sättigt den angegebenen Spieler",
				new Argument("hunger | saturation",
						"Ob Hungerleiste oder Sättigung gefüllt werden soll. Keine Angabe: beides", false),
				new Argument("Spieler", "Der zu sättigende Spieler.", false));
		setAliases("d_feed");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Set<Player> players = new HashSet<>();
		boolean saturation = true;
		boolean hunger = true;
		int count = 0;
		Set<String> unresolvedNames = new HashSet<>();
		if (args.length != 0) {
			if (args[0].equalsIgnoreCase("hunger")) {
				saturation = false;
				args[0] = "%processed%";
			} else if (args[0].equalsIgnoreCase("saturation")) {
				hunger = false;
				args[0] = "%processed%";
			}
			for (String playerName : args) {
				if (Bukkit.getPlayer(playerName) != null) {
					players.add(Bukkit.getPlayer(playerName));
				} else {
					unresolvedNames.add(playerName);
				}
			}
		}
		if (players.isEmpty() && unresolvedNames.isEmpty()) {
			if (sender instanceof Player) {
				players.add((Player) sender);
			} else {
				Main.sendMessagePlayernameRequired(sender);
				return true;
			}
		}
		Main.sendMessagePlayerNotFound(unresolvedNames, sender);
		for (Player current : players) {
			if (hunger) {
				current.setFoodLevel(20);
			}
			if (saturation) {
				current.setSaturation(20);
			}
			Main.getInstance().sendMessage(Main.cConfirm() + "Du wurdest gesättigt!", current);
			count++;
		}
		if (!(players.size() == 1 && players.contains(sender))) {
			Main.getInstance().sendMessage(Main.cValue() + count + Main.cConfirm() + " Spieler gesättigt!", sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length != 0) {
			if (args.length == 1) {
				list.addAll(EssentialCollections.toSortedStringList(EssentialCollections.asList("saturation", "hunger"),
						args[0]));
			}
			list.addAll(Main.getPlayersStartWith(args));
		}
		return list;
	}
}
