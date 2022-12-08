/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.command.bedwars;

import org.bukkit.command.CommandSender;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.map.Map;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;

public class CommandCreateMap extends Command {

	public CommandCreateMap() {
		super(Main.getInstance(), "createMap", new Command[0], "Erstelle eine Map",
				new Argument("map", "Der Mapname"));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			String name = args[0];
			for (Map map : Map.getMaps()) {
				if (map.getName().equals(name)) {
					sender.sendMessage("§cDiese Map existiert bereits!");
					return true;
				}
			}
//			Team team = Team.createTeam(name);
//			sender.sendMessage("§7Du hast das Team §" + team.getNamecolor() + team.getTranslationName() + " erstellt!");
			Map map = new Map(name);
			sender.sendMessage("§7Du hast die Map " + map.getName() + " erstellt!");
			return true;
		}
		return false;
	}
}
