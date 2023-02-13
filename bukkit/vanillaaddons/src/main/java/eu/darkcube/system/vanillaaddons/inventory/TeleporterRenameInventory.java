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
import net.wesjd.anvilgui.AnvilGUI;

public class TeleporterRenameInventory extends AbstractInventory<AnvilGUI, Teleporter> {
	public static final InventoryType TYPE = InventoryType.of("teleporterRename");

	@Override
	protected AnvilGUI openInventory(AUser user) {
		return null;
	}

	@Override
	protected void closeInventory(AUser user, AnvilGUI inventory) {

	}
}
