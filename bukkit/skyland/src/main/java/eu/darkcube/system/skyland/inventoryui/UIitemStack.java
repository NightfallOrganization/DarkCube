/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.inventoryui;

import org.bukkit.inventory.ItemStack;

public class UIitemStack {
	boolean isUnmoveble;
	ItemStack itemStack;

	public UIitemStack(boolean isUnmoveble, ItemStack itemStack) {
		this.isUnmoveble = isUnmoveble;
		this.itemStack = itemStack;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public boolean isUnmoveble() {
		return isUnmoveble;
	}
}
