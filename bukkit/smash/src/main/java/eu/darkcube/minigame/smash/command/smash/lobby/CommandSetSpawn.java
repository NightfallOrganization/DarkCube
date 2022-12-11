/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.command.smash.lobby;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.smash.Main;
import eu.darkcube.minigame.smash.command.CommandArgument;
import eu.darkcube.minigame.smash.util.Arrays;
import eu.darkcube.minigame.smash.util.Locations;
import eu.darkcube.system.commandapi.Command;

public class CommandSetSpawn extends Command {

	public CommandSetSpawn() {
		super(Main.getInstance(), "setSpawn", new Command[0], "Setzt den Lobby-Spawn",
				CommandArgument.MAKE_NICE_LOCATION);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Location loc = p.getLocation();
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("true")) {
					loc = Locations.getNiceLocation(loc);
				}
			}
			p.teleport(loc);
			Main.getInstance().getLobby().setSpawn(loc);
			p.sendMessage("§aSpawn neugesetzt!");
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(Arrays.asList("true", "false"), args[0]);
		}
		return super.onTabComplete(args);
	}
}
