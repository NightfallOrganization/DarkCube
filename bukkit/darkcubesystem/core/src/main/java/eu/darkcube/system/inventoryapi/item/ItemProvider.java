/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.inventoryapi.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ItemProvider {

	ItemBuilder item(Material material);

	ItemBuilder item(ItemStack item);

	ItemBuilder spawner();

}
