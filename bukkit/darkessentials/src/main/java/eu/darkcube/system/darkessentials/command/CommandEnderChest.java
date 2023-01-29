/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;

public class CommandEnderChest extends Command {

	public CommandEnderChest() {
		super(DarkEssentials.getInstance(), "enderchest", new Command[0], "Öffnet die Ender Chest eines Spielers",
				new Argument("Spieler", "Spieler, dessen EnderChest geöffnet werden soll", false));
		setAliases("d_enderchest", "ec");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = null;
		if (args.length == 0 && sender instanceof Player) {
			player = (Player) sender;
		} else if (args.length == 1) {
			if (Bukkit.getPlayer(args[0]) != null) {
				player = Bukkit.getPlayer(args[0]);
			}
		} else if (args.length > 1) {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du darfst nur einen Spieler angeben!", sender);
			return true;
		} else {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst ein Spieler sein um diesen Command auszuführen!",
					sender);
			return true;
		}
		if (player != null) {
			Player playerSender = (Player) sender;
			playerSender.openInventory(player.getEnderChest());
		} else {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Der angegebene Spieler ist nicht online!", sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return DarkEssentials.getPlayersStartWith(args);
		}
		return Collections.emptyList();
	}
}
