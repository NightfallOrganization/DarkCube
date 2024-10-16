/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.shop;

import de.pixel.bedwars.shop.site.ShopSite;
import de.pixel.bedwars.util.Message;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static de.pixel.bedwars.shop.site.ShopSite.*;

public enum ShopType {

	DEFAULT(Message.SHOP_INVENTORY_TITLE, BUILDING_BLOCKS, SWORDS, PICKAXES);

	private final Message title;
	private final ShopSite<?>[] sites;
	private final int sitesSlots;

	ShopType(Message title, ShopSite<?>... sites) {
		this.title = title;
		this.sites = sites;
		this.sitesSlots = ShopItem.getSlots(this.sites.length);
	}

	public static ShopType getType(Player p) {
		return ShopType.DEFAULT;
	}

	public final ShopSite<?>[] getSites() {
		return sites;
	}

	public final Inventory buildInventory(Player p, ShopSite<?> site) {
		final int slots = sitesSlots + 9 + site.getSize();
		final Inventory inv = Bukkit.createInventory(p, slots, title.getMessage(p));
		final ItemStack glassPane =
				ItemBuilder.item(Material.STAINED_GLASS_PANE).damage(7).displayname(" ").build();
		for (int i = 0; i < sites.length; i++) {
			inv.setItem(i, sites[i].getRepresentation().getItem(p));
		}
		for (int i = sitesSlots; i < sitesSlots + 9; i++) {
			inv.setItem(i, glassPane);
		}
		for (int i = slots - site.getSize(), j = 0; i < slots; i++, j++) {
			final ShopItem item = site.getItem(j);
			if (item != null) {
				inv.setItem(i, item.getItem(p));
			}
		}
		return inv;
	}
}
