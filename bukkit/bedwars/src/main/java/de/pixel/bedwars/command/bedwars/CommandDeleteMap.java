/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.command.bedwars;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.map.Map;
import de.pixel.bedwars.util.Arrays;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;

public class CommandDeleteMap extends Command {

	public CommandDeleteMap() {
		super(Main.getInstance(), "deleteMap", new Command[0], "Löscht eine Map", new Argument("map", "Der Mapname"));
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(Map.getMaps(), args[0]);
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			String name = args[0].toUpperCase();
			if (!Map.getAllMapNames().contains(name)) {
				sender.sendMessage("§cUnbekannte Map!");
				return true;
			}
			for (Map map : Map.getMaps()) {
				if (map.getName().equals(name)) {
					sender.sendMessage("§aMap gelöscht!");
					map.delete();
					return true;
				}
			}
		}
		return false;
	}
}
