/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.inventory;

import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.user.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PServerInventory extends DefaultPServerSyncPagedInventory {

	public static final InventoryType TYPE = InventoryType.of("pserver");

	public PServerInventory(User user) {
		super(user, TYPE, Message.PSERVER_INVENTORY_TITLE.getMessage(user.getCommandExecutor()));
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		items.put(0, new ItemStack(Material.DIRT));
		items.put(15, new ItemStack(Material.DIRT));
		items.put(10, new ItemStack(Material.DIRT));
		items.put(20, new ItemStack(Material.DIRT));
		items.put(25, new ItemStack(Material.DIRT));
		items.put(30, new ItemStack(Material.DIRT));
		items.put(42, new ItemStack(Material.DIRT));
	}
}
