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
import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;

public class CommandHat extends Command {

	public CommandHat() {
		super(DarkEssentials.getInstance(), "hat", new Command[0], "Setzt dir ein Item auf den Kopf.",
				new Argument[] { new Argument("Spieler", "Der Spieler, dem das Item aufgesetzt wird.", false) });
		setAliases("d_hat");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0 && !(sender instanceof Player)) {
			DarkEssentials.sendMessagePlayernameRequired(sender);
			return true;
		}
		Set<String> unresolvedNames = new HashSet<>();
		int count = 0;
		for (String playerName : args) {
			if (Bukkit.getPlayer(playerName) != null) {
				Player current = Bukkit.getPlayer(playerName);
				if (current.getItemInHand() != null) {
					ItemStack itemStack = current.getItemInHand();
					current.setItemInHand(current.getInventory().getHelmet());
					current.getInventory().setHelmet(itemStack);
					if (current.equals(sender)) {
						count++;
					}
					DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Du hast einen Hut bekommen!", current);
				} else if (current.equals(sender)) {
					DarkEssentials.getInstance().sendMessage(new StringBuilder().append(
									DarkEssentials.cFail())
							.append("Du musst ein Item in der Hand halten!").toString(), sender);
				}
			} else {
				unresolvedNames.add(playerName);
			}
		}
		DarkEssentials.sendMessagePlayerNotFound(unresolvedNames, sender);
		if (count == 0 && unresolvedNames.isEmpty()) {
			ItemStack itemStack = ((Player) sender).getItemInHand();
			((Player) sender).setItemInHand(((Player) sender).getInventory().getHelmet());
			((Player) sender).getInventory().setHelmet(itemStack);
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Du hast einen Hut bekommen!", (sender));
		} else {
			DarkEssentials.getInstance().sendMessage(new StringBuilder().append(DarkEssentials.cValue()).append(count)
					.append(DarkEssentials.cConfirm()).append(" Spieler haben einen Hut bekommen!").toString(), sender);
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
