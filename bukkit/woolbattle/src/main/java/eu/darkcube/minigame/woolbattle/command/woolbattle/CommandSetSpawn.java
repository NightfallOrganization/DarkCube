/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.Locations;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandSetSpawn extends SubCommand {

	public CommandSetSpawn() {
		super(WoolBattle.getInstance(), "setSpawn", new Command[0], "Setzt den LobbySpawn",
						CommandArgument.ARGUMENT_MAKE_NICE);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length > 1) {
			return false;
		}
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Location loc = p.getLocation();
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("true")) {
					loc = Locations.getNiceLocation(loc);
					p.teleport(loc);
				}
			}
			WoolBattle.getInstance().getLobby().setSpawn(loc);
			sender.sendMessage("§aDer LobbySpawn wurde umgesetzt!");
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(Arrays.asList(true, false), args[0]);
		}
		return super.onTabComplete(args);
	}

}
