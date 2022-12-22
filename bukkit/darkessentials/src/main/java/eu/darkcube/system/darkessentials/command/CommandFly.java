/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.Collections;
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

public class CommandFly extends Command {

	public CommandFly() {
		super(DarkEssentials.getInstance(), "fly", new Command[0], "Lässt einen Spieler fliegen.",
				new Argument("enable|disable|toggle",
						"Ob fliegen aktiviert, deaktiviert oder umgeschaltet werden soll. (Default: toggle", false),
				new Argument("Wert", "Die Geschwindigkleit, mit der der Spieler fliegen kann.", false),
				new Argument("Spieler", "Der Spieler, der fliegen können soll", false));
		setAliases("d_fly");
	}

	private static final String TOGGLE = "toggle";
	private static final String DISABLE = "disable";
	private static final String ENABLE = "enable";
	private static final String PROCESSED = "%processed%";

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Set<Player> players = new HashSet<>();
		Set<String> unresolvedNames = new HashSet<>();
		String action = TOGGLE;
		float value = 1;
		boolean valueSet = false;
		int valuePos = 0;
		int countEnable = 0;
		int countDisable = 0;
		int countSpeed = 0;
		if (args.length != 0) {
			switch (args[0].toLowerCase()) {
			case ENABLE:
				action = ENABLE;
				valuePos = 1;
				args[0] = PROCESSED;
				break;
			case DISABLE:
				action = DISABLE;
				valuePos = 1;
				args[0] = PROCESSED;
				break;
			default:
				return false;
			}
			if (valuePos == 0 || (valuePos == 1 && args.length >= 1)) {
				try {
					value = Float.parseFloat(args[valuePos]);
					args[valuePos] = PROCESSED;
					valueSet = true;
				} catch (NumberFormatException e) {
				}
			}
			for (String playerName : args) {
				if (!playerName.equals(PROCESSED)) {
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
				DarkEssentials.sendMessagePlayernameRequired(sender);
				return true;
			}
		}
		DarkEssentials.sendMessagePlayerNotFound(unresolvedNames, sender);
		for (Player current : players) {
			switch (action) {
			case "enable":
				current.setAllowFlight(true);
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Du kannst nun fliegen!", current);
				countEnable++;
				break;
			case "disable":
				current.setAllowFlight(false);
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Du kannst nun nicht mehr fliegen!", current);
				countDisable++;
				break;
			case TOGGLE:
				if (current.getAllowFlight() && valueSet) {
					break;
				}
				if (!current.getAllowFlight()) {
					current.setAllowFlight(true);
					DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Du kannst nun fliegen!", current);
					countEnable++;
				} else {
					current.setAllowFlight(false);
					countDisable++;
					DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Du kannst nun nicht mehr fliegen!", current);
				}
				break;
			default:
				return false;
			}
			if (valueSet) {
				if (value <= 10 && value >= 0) {
					current.setFlySpeed(value / 10);
					DarkEssentials.getInstance()
							.sendMessage(new StringBuilder().append(DarkEssentials.cConfirm())
									.append("Deine Fluggeschwindigkeit wurde auf ").append(
													DarkEssentials.cValue())
									.append((int) value).append(DarkEssentials.cConfirm()).append(" geändert!").toString(),
									sender);
					countSpeed++;
				}
			}
		}
		if (valueSet && (value > 10 || value < 0)) {
			DarkEssentials.getInstance()
					.sendMessage(new StringBuilder().append(DarkEssentials.cFail()).append("Die angegebene Zahl muss zwischen")
							.append(DarkEssentials.cValue()).append("0 und 10").append(
									DarkEssentials.cFail()).append(" liegen!")
							.toString(), sender);
		}
		if (!(players.size() == 1 && players.contains(sender))) {
			if (countDisable != 0) {
				DarkEssentials.getInstance().sendMessage(new StringBuilder().append(DarkEssentials.cValue()).append(countDisable)
						.append(DarkEssentials.cConfirm()).append(" Spieler können nun fliegen!").toString(), sender);
			}
			if (countEnable != 0) {
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder().append(DarkEssentials.cValue()).append(countDisable)
								.append(DarkEssentials.cConfirm()).append(" Spieler können nun nicht mehr fliegen!").toString(),
								sender);
			}
			if (countSpeed != 0) {
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder().append(DarkEssentials.cConfirm()).append("Geschwindigkeit von ")
								.append(DarkEssentials.cValue()).append(countSpeed).append(
										DarkEssentials.cConfirm())
								.append(" Spielern auf ").append(DarkEssentials.cValue()).append((int) value)
								.append(DarkEssentials.cConfirm()).append(" geändert!").toString(), sender);
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return EssentialCollections.toSortedStringList(EssentialCollections.asList(ENABLE, DISABLE, TOGGLE),
					args[0]);
		}
		if (args.length != 0) {
			return DarkEssentials.getPlayersStartWith(args);
		}
		return Collections.emptyList();
	}
}
