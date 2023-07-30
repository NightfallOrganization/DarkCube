/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Location location = new Location(Bukkit.getWorld("plotworld"), 0.5, 63, 0.5);
			player.teleport(location);
			player.sendMessage("§7Du wurdest zum §bSpawn §7teleportiert!");
		} else {
			sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden!");
		}
		return true;
	}
}
