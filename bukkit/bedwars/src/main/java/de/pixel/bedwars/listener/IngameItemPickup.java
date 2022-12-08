/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.listener;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import de.pixel.bedwars.spawner.ItemSpawner;
import de.pixel.bedwars.team.Team;

public class IngameItemPickup implements Listener {

	@EventHandler
	public void handle(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		CraftItem item = (CraftItem) e.getItem();
		Collection<Player> en = item.getNearbyEntities(1., 1., 1.).stream().filter(s -> s instanceof Player)
				.map(s -> (Player) s).filter(s -> Team.getTeam(s) != Team.SPECTATOR).collect(Collectors.toSet());
		if (item.hasMetadata("spawnerItem")) {
			try {
				ItemSpawner spawner = (ItemSpawner) item.getMetadata("spawnerItem").get(0).value();
				spawner.setSkipCount(spawner.getSkipCount() + en.size() - 1);
				for (Player t : en) {
					if (t != p) {
						Map<Integer, ItemStack> r = t.getInventory().addItem(item.getItemStack());
						if (r.size() != 0) {
							spawner.setSkipCount(spawner.getSkipCount() - r.size());
						}
					}
				}
			} catch (Exception ex) {
			}
		}
	}
}
