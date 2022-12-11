/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.command.bedwars.map;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.map.Map;
import de.pixel.bedwars.util.Arrays;
import de.pixel.bedwars.util.MaterialAndId;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandSetIcon extends SubCommand {
	public CommandSetIcon() {
		super(Main.getInstance(), "setIcon", new Command[0], "Setzt das Icon der Map",
				new Argument("icon", "Das Icon"));
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			List<Material> mats = Arrays.asList(Material.values());
			mats.remove(Material.AIR);
			return Arrays.toSortedStringList(mats, args[0].toUpperCase());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			Map map = Map.getMap(getSpaced());
			if (map == null) {
				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + getSpaced() + "'gefunden werden.");
				return true;
			}
			MaterialAndId icon = MaterialAndId.fromString(args[0]);
			if (icon == null || args[0].startsWith("AIR")) {
				sender.sendMessage("§cDieses Icon ist nicht gültig");
				return true;
			}
			map.setIcon(icon);
			sender.sendMessage("§aDie Map '" + map.getName() + "' hat nun das Icon '" + icon + "'.");
			return true;
		}
		return false;
	}

}
