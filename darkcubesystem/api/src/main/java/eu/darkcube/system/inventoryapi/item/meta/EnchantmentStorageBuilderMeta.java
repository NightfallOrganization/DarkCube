/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.item.meta;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentStorageBuilderMeta implements BuilderMeta {
	private final Map<Enchantment, Integer> enchantments = new HashMap<>();

	public EnchantmentStorageBuilderMeta() {
	}

	public EnchantmentStorageBuilderMeta(Map<Enchantment, Integer> enchantments) {
		this.enchantments.putAll(enchantments);
	}

	public EnchantmentStorageBuilderMeta enchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments.clear();
		this.enchantments.putAll(enchantments);
		return this;
	}

	public Map<Enchantment, Integer> enchantments() {
		return enchantments;
	}

	@Override
	public BuilderMeta clone() {
		return new EnchantmentStorageBuilderMeta(enchantments);
	}
}
