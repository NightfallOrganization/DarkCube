/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.listener;

import eu.darkcube.system.inventoryapi.ItemBuilder;
import eu.darkcube.system.pserver.plugin.inventory.UserManagmentInventory;
import eu.darkcube.system.pserver.plugin.inventory.UserManagmentUserInventory;
import eu.darkcube.system.pserver.plugin.user.User;
import eu.darkcube.system.pserver.plugin.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class UserManagmentInventoryListener implements BaseListener {

	private final UserManagmentInventory inventory;

	public UserManagmentInventoryListener(UserManagmentInventory inventory) {
		this.inventory = inventory;
	}

	@EventHandler
	public void handle(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getWhoClicked();
		User user = UserManager.getInstance().getUser(player.getUniqueId());
		if (!inventory.isOpened(player)) {
			return;
		}
		ItemStack item = event.getCurrentItem();
		if (item == null) {
			return;
		}
		ItemBuilder builder = new ItemBuilder(item);
		ItemBuilder.Unsafe unsafe = builder.getUnsafe();
		if (unsafe.getString(UserManagmentInventory.KEY) == null) {
			return;
		}
		if (!unsafe.getString(UserManagmentInventory.KEY).equals(UserManagmentInventory.KEY_VALUE)) {
			return;
		}
		UUID uuid = UUID.fromString(unsafe.getString(UserManagmentInventory.USER_UUID_KEY));
		String name = unsafe.getString(UserManagmentInventory.USER_NAME_KEY);
		new UserManagmentUserInventory(user, uuid, name).open();
	}
}
