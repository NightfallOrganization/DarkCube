/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.inventoryUI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryUI {

	UIitemStack[][] inv;
	String name;

	public InventoryUI() {

	}

	public void openInv(Player p, int size) {
		//Inventory inventory = Bukkit.createInventory(p, inv.length*inv[0].length, name);
		Inventory inventory = Bukkit.createInventory(p, size, "name");

		for (UIitemStack[] row : inv) {
			for (UIitemStack uiis : row) {
				inventory.addItem(uiis.getItemStack());
			}
		}

		p.openInventory(inventory);
	}

}
