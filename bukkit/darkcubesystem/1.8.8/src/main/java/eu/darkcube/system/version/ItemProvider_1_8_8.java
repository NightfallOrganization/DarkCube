/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.item.ItemProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemProvider_1_8_8 implements ItemProvider {
	@Override
	public ItemBuilder item(Material material) {
		return new ItemBuilder_1_8_8().material(material);
	}

	@Override
	public ItemBuilder item(ItemStack item) {
		return new ItemBuilder_1_8_8(item);
	}

	@Override
	public ItemBuilder spawner() {
		return item(Material.MOB_SPAWNER);
	}
}
