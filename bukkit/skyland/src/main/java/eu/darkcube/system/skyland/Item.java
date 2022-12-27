/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import eu.darkcube.system.inventoryapi.ItemBuilder;
import eu.darkcube.system.userapi.User;
import org.bukkit.inventory.ItemStack;

import static eu.darkcube.system.inventoryapi.ItemBuilder.*;
import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

public enum Item {

	STARTER_SWORD(item(STONE_SWORD), 13),
	NETHERBLOCK_CHESTPLATE(
			item(NETHERITE_CHESTPLATE).enchant(MENDING, 1).enchant(PROTECTION_ENVIRONMENTAL, 10)
					.enchant(DURABILITY, 10)),
	MINING_CHESTPLATE(item(LEATHER_CHESTPLATE)),
	SPEED_CHESTPLATE(item(LEATHER_CHESTPLATE)),
	POOP(item(COCOA_BEANS)),
	;
	private final ItemBuilder builder;
	private final String key;
	private final int damage;

	Item(ItemBuilder builder) {
		this(builder, -1);
	}

	Item(ItemBuilder builder, int damage) {
		this.builder = builder;
		this.damage = damage;
		this.key = name();
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItem(User user) {
		String itemTitle = Message.getMessage(key, user);
		ItemBuilder clone = new ItemBuilder(builder);
		clone.displayname(itemTitle);
		if (damage != -1) {
			clone.lore(Message.getMessage("ITEM_ATTRIBUTE_DAMAGE", user, damage));
		}
		return clone.build();
	}
}
