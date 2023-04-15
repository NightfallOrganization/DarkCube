/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.lobbysystem.inventory.abstraction.MinigameInventory;
import eu.darkcube.system.lobbysystem.util.UUIDManager;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class ListenerMinigameServer extends BaseListener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		String itemid = ItemBuilder.item(item).persistentDataStorage()
				.get(MinigameInventory.minigameServer, PersistentDataTypes.STRING);
		if (itemid == null || itemid.isEmpty()) {
			return;
		}
		p.closeInventory();
		UUIDManager.getManager().getPlayerExecutor(p.getUniqueId()).connect(Objects.requireNonNull(
				CloudNetDriver.getInstance().getCloudServiceProvider()
						.getCloudService(UUID.fromString(itemid))).getServiceId().getName());
	}
}
