/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version.v1_19_3;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemProvider implements eu.darkcube.system.inventoryapi.item.ItemProvider {
	@Override
	public eu.darkcube.system.inventoryapi.item.ItemBuilder item(Material material) {
		return new ItemBuilder().material(material);
	}

	@Override
	public eu.darkcube.system.inventoryapi.item.ItemBuilder item(ItemStack item) {
		return new ItemBuilder(item);
	}

	@Override
	public eu.darkcube.system.inventoryapi.item.ItemBuilder spawner() {
		return item(Material.SPAWNER);
	}
}
