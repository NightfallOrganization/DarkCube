/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.inventory;

import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.vanillaaddons.AUser;
import eu.darkcube.system.vanillaaddons.util.Teleporter;
import org.bukkit.inventory.Inventory;

public class TeleporterInventory extends AbstractInventory<Inventory, Teleporter> {
	public static final InventoryType TYPE = InventoryType.of("teleporter");

	@Override
	protected Inventory openInventory(AUser user) {
		return null;
	}

	@Override
	protected void closeInventory(AUser user, Inventory inventory) {

	}
}
