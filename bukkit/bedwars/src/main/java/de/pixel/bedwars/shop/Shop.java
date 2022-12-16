/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.shop;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.shop.site.ShopSite;

public abstract class Shop {

	static Collection<Player> open = new HashSet<>();

	public abstract void registerOpenListener();

	public abstract void unregisterOpenListener();

	public Shop() {
		registerOpenListener();
	}

	public static final void open(Player p) {
		open(p, ShopSite.BUILDING_BLOCKS);
	}

	public static final void open(Player p, ShopSite<?> site) {
		ShopType type = ShopType.getType(p);
		Inventory inv = type.buildInventory(p, site);
		p.openInventory(inv);
		open.add(p);
	}

	static {
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), Main.getInstance());
	}

	private static class PlayerListener implements Listener {

		@EventHandler
		public void handle(InventoryCloseEvent e) {
			open.remove(e.getPlayer());
		}
	}
}
