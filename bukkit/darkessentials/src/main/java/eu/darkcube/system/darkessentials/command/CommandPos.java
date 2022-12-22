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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;

public class CommandPos extends Command {

	public CommandPos() {
		super(DarkEssentials.getInstance(), "position", new Command[0], "Gibt die Position eines Spielers.",
				new Argument[] { new Argument("Spieler", "Der Spieler, dessen Position gezeigt werden soll.", false) });
		setAliases("d_position", "pos");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0 && sender instanceof Player) {
			Location loc = ((Player) sender).getLocation();
			DarkEssentials.getInstance().sendMessage(new StringBuilder().append(DarkEssentials.cConfirm()).append("Du bist bei §7[")
					.append(DarkEssentials.cValue()).append(Math.round(loc.getX())).append(", ").append(Math.round(loc.getY()))
					.append(", ").append(Math.round(loc.getZ())).append("§7]").append(
							DarkEssentials.cConfirm())
					.append(" in der Welt §7\"").append(DarkEssentials.cValue()).append(((Player) sender).getWorld().getName())
					.append(DarkEssentials.cConfirm()).append("§7\"").toString(), sender);
		} else if (args.length != 0) {
			Set<String> unresolvedNames = new HashSet<>();
			for (String playerName : args) {
				if (Bukkit.getPlayer(playerName) != null) {
					Location loc = Bukkit.getPlayer(playerName).getLocation();
					DarkEssentials.getInstance()
							.sendMessage(new StringBuilder().append(DarkEssentials.cConfirm()).append("Der Spieler ")
									.append(Bukkit.getPlayer(playerName).getName()).append(" ist bei §7[")
									.append(DarkEssentials.cValue()).append(Math.round(loc.getX())).append(", ")
									.append(Math.round(loc.getY())).append(", ").append(Math.round(loc.getZ()))
									.append("§7]").append(DarkEssentials.cConfirm()).append(" in der Welt §7\"")
									.append(DarkEssentials.cValue()).append(Bukkit.getPlayer(playerName).getWorld().getName())
									.append(DarkEssentials.cConfirm()).append("§7\"").toString(), sender);
				} else {
					unresolvedNames.add(playerName);
				}
			}
			DarkEssentials.sendMessagePlayerNotFound(unresolvedNames, sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length != 0)
			return DarkEssentials.getPlayersStartWith(args);
		return new ArrayList<>();
	}
}
