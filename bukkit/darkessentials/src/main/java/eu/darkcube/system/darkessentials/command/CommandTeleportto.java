/*
 * Copyright (c) 2022. [DarkCube]
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
import eu.darkcube.system.darkessentials.Main;

public class CommandTeleportto extends Command {

	public CommandTeleportto() {
		super(Main.getInstance(), "teleportto", new Command[0], "Teleportiert dich zu einem Spieler.",
				new Argument("Spieler", "Der Spieler, zu dem du dich teleportierst."));
		setAliases("tpto", "d_teleportto");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0 || args.length > 1)
			return false;
		if (!(sender instanceof Player)) {
			Main.getInstance().sendMessage(Main.cFail() + "Du musst ein Spieler sein, um diesen Command auszuführen!",
					sender);
		} else if (Bukkit.getPlayer(args[0]) != null) {
			Main.getInstance().sendMessage(Main.colorConfirm + "Du wurdest zu " + Main.colorValue
					+ Bukkit.getPlayer(args[0]).getName() + Main.colorConfirm + " teleportiert!", sender);
			((Player) sender).teleport(Bukkit.getPlayer(args[0]));
		} else {
			Main.getInstance().sendMessage(Main.colorFail + "Der Spieler " + Main.colorValue + args[0] + Main.colorFail
					+ " wurde nicht gefunden!", sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1)
			return Main.getPlayersStartWith(args);
		return new ArrayList<>();
	}

}
