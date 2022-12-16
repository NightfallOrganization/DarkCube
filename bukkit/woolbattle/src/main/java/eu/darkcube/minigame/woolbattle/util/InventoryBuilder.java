/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryBuilder {

	private String title;
	private InventoryType type;
	private int slots;
	private int maxStackSize;
	private Map<Integer, ItemStack> items;

	public InventoryBuilder(String title) {
		this.title = title;
		this.type = InventoryType.CHEST;
		this.items = new HashMap<>();
		this.slots = -1;
		this.maxStackSize = 64;
	}

	public InventoryBuilder setItem(int slot, ItemStack item) {
		items.put(slot, item);
		return this;
	}

	public InventoryBuilder removeItem(int slot) {
		items.remove(slot);
		return this;
	}

	public InventoryBuilder type(InventoryType type) {
		this.type = type;
		return this;
	}

	public InventoryBuilder size(int slots) {
		this.slots = slots;
		if (this.slots == 0)
			this.slots = 9;
		while (this.slots % 9 != 0)
			this.slots++;
		return this;
	}

	public InventoryBuilder maxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
		return this;
	}

	public Inventory build() {
		Inventory inv = null;
		if (this.slots == -1) {
			inv = Bukkit.createInventory(null, type, title);
		} else {
			inv = Bukkit.createInventory(null, slots, title);
		}
		for (int slot : items.keySet()) {
			inv.setItem(slot, items.get(slot));
		}
		inv.setMaxStackSize(maxStackSize);
		return inv;
	}
}
