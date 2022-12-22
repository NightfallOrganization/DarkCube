/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.darkessentials.util.GamemodeChanger;

public class CommandGamemodeAll extends Command {

	public CommandGamemodeAll() {
		super(DarkEssentials.getInstance(), "gamemodeall", new Command[0], "Setzt den Gamemode aller Spieler",
				new Argument("gamemode", "Der  Gamemode, der gesetzt werden soll."));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 1)
			return false;
		int gamemode = 0;
		switch (args[0].toLowerCase()) {
		case "survival":
		case "s":
		case "su":
		case "0":
			break;
		case "creative":
		case "c":
		case "1":
			gamemode = 1;
			break;
		case "adventure":
		case "a":
		case "2":
			gamemode = 2;
			break;
		case "spectator":
		case "sp":
		case "3":
			gamemode = 3;
			break;
		default:
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst einen Gamemode angeben!", sender);
			return true;
		}
		for (Player p : Bukkit.getOnlinePlayers())
			GamemodeChanger.changeGamemode(gamemode, sender, p);
		return true;
	}

}
