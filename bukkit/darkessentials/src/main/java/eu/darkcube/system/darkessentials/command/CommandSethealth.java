/*
 * Copyright (c) 2022-2023. [DarkCube]
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
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandSethealth extends Command {

	public CommandSethealth() {
		super(DarkEssentials.getInstance(), "sethealth", new Command[0], "Legt die maximalen Leben eines Spielers fest.",
				new Argument("Wert", "Anzahl der Leben, die ein Spieler haben soll (in halben Herzen)"),
				new Argument("Spieler", "Der Spieler, dessen Leben gesetzt werden sollen.", false));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		int value;
		if (args[0].equalsIgnoreCase("reset")) {
			value = 20;
		} else {
			try {
				value = Integer.parseInt(args[0]);
				args[0] = "%processed%";
			} catch (Exception e) {
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst eine Zahl angeben!", sender);
				return true;
			}
			if (value <= 0) {
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Die angegebene Zahl muss positiv sein!", sender);
				return true;
			}
		}
		Set<Player> players = new HashSet<>();
		Set<String> unresolvedNames = new HashSet<>();
		for (String playerName : args) {
			if (!playerName.equals("%processed%")) {
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
				DarkEssentials.sendMessagePlayernameRequired(sender);
				return true;
			}
		}
		DarkEssentials.sendMessagePlayerNotFound(unresolvedNames, sender);
		int count = 0;
		for (Player current : players) {
			current.setMaxHealth(value);
			count++;
		}
		if (!(players.size() == 1 && players.contains(sender) && unresolvedNames.isEmpty())) {
			DarkEssentials.getInstance()
					.sendMessage(new StringBuilder(DarkEssentials.cConfirm()).append("Leben von ").append(
									DarkEssentials.cValue())
							.append(count).append(DarkEssentials.cConfirm()).append(" Spielern auf ").append(
									DarkEssentials.cValue())
							.append(value).append(DarkEssentials.cConfirm()).append(" gesetzt!").toString(), sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1)
			return EssentialCollections.asList("reset");
		if (args.length > 1)
			return DarkEssentials.getPlayersStartWith(args);
		return new ArrayList<>();
	}

}
