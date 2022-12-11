/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;

public class CommandBurn extends Command {

	public CommandBurn() {
		super(Main.getInstance(), "burn", new Command[0], "Lässt Spieler brennen.",
				new Argument("Spieler", "Der Spieler, der brennen soll.", false),
				new Argument("Dauer", "Die Dauer des Brennens in Sekunden. (Keine Angabe: Für immer)", false));
		setAliases("d_burn");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Set<Player> players = new HashSet<>();
		int ticks = Integer.MAX_VALUE;
		int count = 0;
		Set<String> unresolvedNames = new HashSet<>();
		if (args.length != 0) {
			try {
				ticks = Integer.parseInt(args[0]) * 20;
				args[0] = "%processed%";
			} catch (Exception e) {
			}
			for (String playerName : args) {
				if (!playerName.equals("%processed%")) {
					if (Bukkit.getPlayer(playerName) != null) {
						players.add(Bukkit.getPlayer(playerName));
					} else {
						unresolvedNames.add(playerName);
					}
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
			current.setFireTicks(ticks);
			if (ticks == Integer.MAX_VALUE) {
				Main.getInstance().sendMessage(Main.cConfirm() + "Du wurdest angezündet!", sender);
			} else {
				Main.getInstance().sendMessage(
						new StringBuilder().append(Main.cConfirm()).append("Du wurdest für ").append(Main.cValue())
								.append(ticks / 20).append(Main.cConfirm()).append(" Sekunden angezündet!").toString(),
						sender);
			}
			count++;
		}
		if (!(players.size() == 1 && players.contains(sender))) {
			if (ticks == Integer.MAX_VALUE) {
				Main.getInstance().sendMessage(Main.cValue() + count + Main.cConfirm() + " Spieler angezündet!",
						sender);
			} else {
				Main.getInstance()
						.sendMessage(new StringBuilder().append(Main.cValue()).append(count).append(Main.cConfirm())
								.append(" Spieler für ").append(Main.cValue()).append(ticks / 20)
								.append(Main.cConfirm()).append(" Sekunden angezündet!").toString(), sender);
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length != 0)
			return Main.getPlayersStartWith(args);
		return Collections.emptyList();
	}
}
