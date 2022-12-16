/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.command.bedwars.spawner;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.spawner.ItemSpawner;
import de.pixel.bedwars.spawner.io.SpawnerIO;
import eu.darkcube.system.commandapi.Command;

public class CommandDelete extends Command {

	public CommandDelete() {
		super(Main.getInstance(), "delete", new Command[0], "Entfernt einen Spawner");
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		Block block = p.getLocation().subtract(0, 1, 0).getBlock();
		for (String spawnerString : SpawnerIO.getSpawnersStringList()) {
			ItemSpawner spawner = SpawnerIO.fromString(spawnerString);
			if (spawner.getSpawnerBlock().equals(block)) {
				sender.sendMessage("§aSpawner entfernt!");
				SpawnerIO.remove(spawner);
				return true;
			}
		}
		sender.sendMessage("§cUnter dir ist kein Spawner!");
		return true;
	}
}
