/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.inventoryUI;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UICrafting extends InventoryUI{
	public UICrafting(int row, String name, Player p) {
		super(row, name, p);
	}

	@Override
	public boolean invClickEvent(InventoryClickEvent e) {

		
		return super.invClickEvent(e);
	}
}
