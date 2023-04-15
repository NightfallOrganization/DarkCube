/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;

public class CommandInvsee extends Command {

	public CommandInvsee() {
		super(DarkEssentials.getInstance(), "invsee", new Command[0], "Zeigt das Inventar eines Spielers.",
				new Argument[] { new Argument("Spieler", "Der Spieler, dessen Inventar gezeigt werden soll.") });
		setAliases("d_invsee");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		if (args.length > 1) {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du darfst nur einen Spielernamen angeben!");
			return true;
		}
		if (!(sender instanceof Player)) {
			DarkEssentials.getInstance().sendMessage(
					DarkEssentials.cFail() + "Du bist kein Spieler, deshalb kannst du diesen Command nicht ausführen!");
			return true;
		}
		if (Bukkit.getPlayer(args[0]) != null) {
			((Player) sender).openInventory(Bukkit.getPlayer(args[0]).getInventory());
		} else {
			DarkEssentials.getInstance()
					.sendMessage(new StringBuilder().append(DarkEssentials.cFail()).append("Der Spieler §7\"").append(
											DarkEssentials.cValue()).append(args[0])
							.append("§7\"").append(DarkEssentials.cFail()).append(" konnte nicht gefunden werden!").toString(),
							sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1)
			return DarkEssentials.getPlayersStartWith(args);
		return new ArrayList<>();
	}

}
