/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.Map;
import org.bukkit.inventory.ItemStack;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;

public class InventoryGameServersSelection extends LobbyAsyncPagedInventory {

	private static final InventoryType type_gameserver_selection =
			InventoryType.of("gameserver_selection");

	public InventoryGameServersSelection(User user) {
		super(InventoryGameServersSelection.type_gameserver_selection,
				Item.GAME_PSERVER.getDisplayName(user), user);
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5),
				Item.GAME_PSERVER.getItem(this.user.getUser()));
		super.insertFallbackItems();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		items.put(this.getPageSize() / 2,
				Item.GAMESERVER_SELECTION_WOOLBATTLE.getItem(this.user.getUser()));
	}

}
