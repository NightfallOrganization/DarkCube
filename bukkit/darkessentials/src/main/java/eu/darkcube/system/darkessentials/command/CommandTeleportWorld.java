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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandTeleportWorld extends Command {

	public CommandTeleportWorld() {
		super(DarkEssentials.getInstance(), "teleportworld", new Command[0], "Teleportiert dich in eine bestimmte Welt.",
				new Argument("Welt", "Die Welt, in die du dich teleportierst."),
				new Argument("x", "Die X-Koordinate", false), new Argument("y", "Die Y-Koordinate", false),
				new Argument("z", "Die Z-Koordinate", false));
		setAliases("tpworld", "d_teleportworld", "d_tpworld", "tpw", "d_tpw");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0 || args.length > 4 || (args.length < 4 && args.length != 1))
			return false;
		if (!(sender instanceof Player)) {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.colorFail + "Du musst ein Spieler sein, um diesen Command auszuführen!",
					sender);
		} else if (Bukkit.getWorld(args[0]) != null) {
			Location loc = Bukkit.getWorld(args[0]).getSpawnLocation().clone();
			if (args.length == 4) {
				try {
					loc.setX(Double.parseDouble(args[1]));
					loc.setY(Double.parseDouble(args[2]));
					loc.setZ(Double.parseDouble(args[3]));
				} catch (NumberFormatException ex) {
					DarkEssentials.getInstance().sendMessage(DarkEssentials.colorFail + "Du musst eine Zahl angeben!", sender);
					return true;
				}
			}
			((Player) sender).teleport(loc);
			DarkEssentials.getInstance().sendMessage(
					DarkEssentials.colorConfirm + "Du wurdest in die Welt " + DarkEssentials.colorValue
					+ Bukkit.getWorld(args[0]).getName() + DarkEssentials.colorConfirm + " teleportiert!", sender);
		} else {
			DarkEssentials.getInstance().sendMessage(
					DarkEssentials.colorFail + "Die Welt " + DarkEssentials.colorValue + args[0] + DarkEssentials.colorFail
					+ " wurde nicht gefunden!", sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1)
			return EssentialCollections.toSortedStringList(Bukkit.getWorlds(), EssentialCollections.asList(args),
					args[0]);
		return new ArrayList<>();
	}

}
