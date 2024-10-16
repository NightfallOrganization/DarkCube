/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.listener;

import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.vanillaaddons.AUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

	@EventHandler
	public void handle(InventoryCloseEvent event) {
		AUser user = AUser.user(UserAPI.getInstance().getUser(event.getPlayer().getUniqueId()));
		user.openInventory(null, null);
	}
}
