/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.command.bedwars.spawner;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.spawner.ItemSpawner;
import de.pixel.bedwars.spawner.ItemSpawnerGold;
import de.pixel.bedwars.spawner.ItemSpawnerIron;
import de.pixel.bedwars.spawner.io.SpawnerIO;
import eu.darkcube.system.commandapi.Command;

public class CommandList extends Command {

	public CommandList() {
		super(Main.getInstance(), "list", new Command[0], "Listet alle Spawner auf");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		sender.sendMessage("§8---------------------------------------------");
		for (String spawnerString : SpawnerIO.getSpawnersStringList()) {
			ItemSpawner spawner = SpawnerIO.fromString(spawnerString);
			sender.sendMessage(spawner.getClass().getSimpleName() + ": " + spawner.getSpawnerBlock().getX() + ","
					+ spawner.getSpawnerBlock().getY() + "," + spawner.getSpawnerBlock().getZ() + ","
					+ spawner.getSpawnerBlock().getWorld().getName());
			if (sender instanceof Player) {
				Material mat = spawner instanceof ItemSpawnerGold ? Material.GOLD_BLOCK
						: spawner instanceof ItemSpawnerIron ? Material.IRON_BLOCK : Material.BRICK;
				((Player) sender).sendBlockChange(spawner.getSpawnerBlock().getLocation(), mat, (byte) 0);
			}
		}
		sender.sendMessage("§8---------------------------------------------");
		return true;
	}
}
