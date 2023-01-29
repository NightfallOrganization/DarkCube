/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.pserver;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelectionWoolBattle;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.userapi.User;
import org.bukkit.Material;

public class PServerDataManager {

	public static ItemBuilder getDisplayItem(User user, UniqueId pserverId) {
		if (pserverId != null) {
			JsonDocument data = PServerProvider.getInstance().getPServerData(pserverId);
			if (data.contains("task")) {
				ItemBuilder b =
						PServerDataManager.getDisplayItemGamemode(user, data.getString("task"));
				b.lore(Component.text("ID: " + pserverId).color(NamedTextColor.GRAY));
				return b;
			}
			ItemBuilder b = ItemBuilder.item(SkullCache.getCachedItem(user.getUniqueId()));
			b.displayname(Item.WORLD_PSERVER.getDisplayName(user));
			b.lore(Component.text("ID: " + pserverId).color(NamedTextColor.GRAY));
			return b;
		}
		return null;
	}

	public static ItemBuilder getDisplayItemGamemode(User user, String task) {
		if (task == null) {
			return null;
		}
		if (Lobby.getInstance().getDataManager().getWoolBattleTasks().contains(task)) {
			ServiceTask stask =
					CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(task);
			if (stask != null) {
				return new InventoryGameServerSelectionWoolBattle.Func().apply(user, stask);
			}
		}
		ItemBuilder b = ItemBuilder.item(Material.BARRIER);
		b.displayname(Component.text("Task not found: " + task).color(NamedTextColor.RED));
		return b;
	}
}
