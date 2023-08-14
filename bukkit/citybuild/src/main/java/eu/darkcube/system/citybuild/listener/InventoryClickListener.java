/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Random;

public class InventoryClickListener implements Listener {

	private static final Random RANDOM = new Random();
	private static final int MAX_WORLD_COORDINATE = 5000;
	private static final int MIN_WORLD_HEIGHT = 50;
	private static final int MAX_WORLD_HEIGHT = 150;

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getView().getTitle().equals("\uDAFF\uDFEF§fḇ")) {
			event.setCancelled(true);

			if (event.getCurrentItem().getType() == Material.DEEPSLATE_TILE_SLAB &&
					event.getCurrentItem().getItemMeta().getDisplayName().equals("§6Plot")) {
				Player player = (Player) event.getWhoClicked();
				player.performCommand("plot home");
				player.sendMessage("§7Du wurdest zu deinem §6Plot §7teleportiert!");

			} else if (event.getCurrentItem().getType() == Material.NETHER_STAR &&
					event.getCurrentItem().getItemMeta().getDisplayName().equals("§bSpawn")) {
				Player player = (Player) event.getWhoClicked();
				player.performCommand("spawn");

			} else if (event.getCurrentItem().getType() == Material.CRIMSON_NYLIUM &&
					event.getCurrentItem().getItemMeta().getDisplayName().equals("§4Nether")) {
				Player player = (Player) event.getWhoClicked();
				int x = RANDOM.nextInt(MAX_WORLD_COORDINATE * 2) - MAX_WORLD_COORDINATE;
				int y = RANDOM.nextInt(MAX_WORLD_HEIGHT - MIN_WORLD_HEIGHT + 1) + MIN_WORLD_HEIGHT;
				int z = RANDOM.nextInt(MAX_WORLD_COORDINATE * 2) - MAX_WORLD_COORDINATE;
				World nether = Bukkit.getServer().getWorld("nether");
				if (nether != null) {
					player.teleport(new Location(nether, x, y, z));
					player.sendMessage("§7Du wurdest in den §4Nether §7teleportiert!");
				}

			} else if (event.getCurrentItem().getType() == Material.GRASS_BLOCK &&
					event.getCurrentItem().getItemMeta().getDisplayName().equals("§aBeastrealm")) {
				Player player = (Player) event.getWhoClicked();
				World beastrealm = Bukkit.getServer().getWorld("Beastrealm");
				if (beastrealm != null) {
					player.teleport(new Location(beastrealm, 0.5, 72, 0.5));
					player.sendMessage("§7Du wurdest in die §aBeastrealm §7teleportiert!");
				}
			}
		}
	}
}
