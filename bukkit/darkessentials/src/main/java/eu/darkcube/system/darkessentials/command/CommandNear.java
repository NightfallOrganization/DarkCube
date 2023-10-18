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
import eu.darkcube.system.darkessentials.util.KesUtils;

public class CommandNear extends Command {

	public CommandNear() {
		super(DarkEssentials.getInstance(), "near", new Command[0], "Gibt den nächsten Spieler wieder.", new Argument[] {
				new Argument("Spieler", "Der Spieler, dessen nächster Spieler angezeit wird.", false) });
		setAliases("d_near");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				DarkEssentials.sendMessagePlayernameRequired(sender);
				return true;
			}
			if (getClosestPlayer((Player) sender) != null) {
				Player closest = getClosestPlayer((Player) sender);
				DarkEssentials.getInstance().sendMessage(
						new StringBuilder().append(DarkEssentials.cConfirm()).append("Der nächste Spieler ist ")
								.append(DarkEssentials.cValue()).append(closest.getName()).append(
										DarkEssentials.cConfirm()).append(" (")
								.append(DarkEssentials.cValue())
								.append((int) KesUtils.getDistance(((Player) sender).getLocation(),
										closest.getLocation()))
								.append(DarkEssentials.cConfirm()).append(" Blöcke).").toString(),
						sender);
			} else {
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Es befindet sich kein Spieler in deiner Nähe!", sender);
			}
			return true;
		}
		Set<String> unresolvedNames = new HashSet<>();
		for (String playerName : args) {
			if (Bukkit.getPlayer(playerName) != null) {
				Player current = Bukkit.getPlayer(playerName);
				if (getClosestPlayer(current) != null) {
					Player closest = getClosestPlayer(current);
					DarkEssentials.getInstance()
							.sendMessage(new StringBuilder().append(DarkEssentials.cConfirm()).append("Der nächste Spieler von ")
									.append(current.getName()).append(" ist ").append(
											DarkEssentials.cValue())
									.append(closest.getName()).append(DarkEssentials.cConfirm()).append(" (")
									.append(DarkEssentials.cValue())
									.append((int) KesUtils.getDistance(current.getLocation(), closest.getLocation()))
									.append(DarkEssentials.cConfirm()).append(" Blöcke).").toString(), sender);
				} else {
					DarkEssentials.getInstance().sendMessage(
							DarkEssentials.cFail() + "Es befindet sich kein Spieler in der Nähe von " + current.getName() + "!",
							sender);
				}
			} else {
				unresolvedNames.add(playerName);
			}
		}
		return true;
	}

	private Player getClosestPlayer(Player pl) {
		double dist = Double.MAX_VALUE;
		Player closest = null;
		for (Player current : Bukkit.getOnlinePlayers()) {
			if (KesUtils.getDistance(pl.getLocation(), current.getLocation()) < dist && !current.equals(pl)) {
				closest = current;
				dist = KesUtils.getDistance(pl.getLocation(), current.getLocation());
			}
		}
		return closest;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length != 0)
			return DarkEssentials.getPlayersStartWith(args);
		return new ArrayList<>();
	}
}
