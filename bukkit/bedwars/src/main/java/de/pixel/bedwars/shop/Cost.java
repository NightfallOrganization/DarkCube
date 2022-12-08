/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.pixel.bedwars.util.Message;
import eu.darkcube.system.inventory.api.util.ItemBuilder;

public class Cost {

	public static final Cost GOLD = new Cost(new ItemBuilder(Material.GOLD_INGOT).build(), Message.GOLD);
	public static final Cost IRON = new Cost(new ItemBuilder(Material.IRON_INGOT).build(), Message.IRON);
	public static final Cost BRONZE = new Cost(new ItemBuilder(Material.CLAY_BRICK).build(), Message.BRONZE);
	public static final Cost NONE = new Cost(new ItemBuilder(Material.AIR).build(), Message.NONE);

	private final ItemStack item;
	private final Message translation;
	private final int count;

	private Cost(final ItemStack item, Message translation) {
		this.item = item;
		this.count = this.item.getAmount();
		this.item.setAmount(1);
		this.translation = translation;
	}
	
	public final Cost of(final int amount) {
		return this.amount(amount);
	}

	public final Cost amount(final int amount) {
		ItemStack item = this.getItem();
		item.setAmount(amount);
		return new Cost(item, getTranslation());
	}
	
	public final Message getTranslation() {
		return this.translation;
	}

	public final int getCount() {
		return this.count;
	}

	public final ItemStack getItem() {
		return this.item.clone();
	}
}
