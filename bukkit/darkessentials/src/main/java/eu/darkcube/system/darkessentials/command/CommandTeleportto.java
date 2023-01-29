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

public class CommandTeleportto extends Command {

	public CommandTeleportto() {
		super(DarkEssentials.getInstance(), "teleportto", new Command[0], "Teleportiert dich zu einem Spieler.",
				new Argument("Spieler", "Der Spieler, zu dem du dich teleportierst."));
		setAliases("tpto", "d_teleportto");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0 || args.length > 1)
			return false;
		if (!(sender instanceof Player)) {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst ein Spieler sein, um diesen Command auszuführen!",
					sender);
		} else if (Bukkit.getPlayer(args[0]) != null) {
			DarkEssentials.getInstance().sendMessage(
					DarkEssentials.colorConfirm + "Du wurdest zu " + DarkEssentials.colorValue
					+ Bukkit.getPlayer(args[0]).getName() + DarkEssentials.colorConfirm + " teleportiert!", sender);
			((Player) sender).teleport(Bukkit.getPlayer(args[0]));
		} else {
			DarkEssentials.getInstance().sendMessage(
					DarkEssentials.colorFail + "Der Spieler " + DarkEssentials.colorValue + args[0] + DarkEssentials.colorFail
					+ " wurde nicht gefunden!", sender);
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
