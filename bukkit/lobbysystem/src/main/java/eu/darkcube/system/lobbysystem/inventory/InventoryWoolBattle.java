/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory;

import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.MinigameInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;

import java.util.Set;

public class InventoryWoolBattle extends MinigameInventory {

	private static final InventoryType type_woolbattle = InventoryType.of("woolbattle");

	public InventoryWoolBattle(User user) {
		super(Item.INVENTORY_COMPASS_WOOLBATTLE.getDisplayName(user),
				Item.INVENTORY_COMPASS_WOOLBATTLE, InventoryWoolBattle.type_woolbattle, user);
	}

	// @Override
	// protected ItemStack getMinigameItem(User user) {
	// return Item.INVENTORY_COMPASS_WOOLBATTLE.getItem(user);
	// }

	@Override
	protected Set<String> getCloudTasks() {
		return Lobby.getInstance().getDataManager().getWoolBattleTasks();
	}

}
