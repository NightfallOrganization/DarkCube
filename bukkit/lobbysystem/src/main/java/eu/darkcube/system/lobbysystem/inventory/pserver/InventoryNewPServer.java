/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.inventory.pserver;

import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.inventory.InventoryLoading;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.pserver.common.PServerBuilder;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;
import eu.darkcube.system.userapi.User;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class InventoryNewPServer extends LobbyAsyncPagedInventory {

	private static final InventoryType type_new_pserver = InventoryType.of("new_pserver");

	public InventoryNewPServer(User user) {
		super(InventoryNewPServer.type_new_pserver, Item.INVENTORY_NEW_PSERVER.getDisplayName(user),
				user);
	}

	@Override
	protected void inventoryClick(IInventoryClickEvent event) {
		event.setCancelled(true);
		if (event.item() == null)
			return;
		String itemid = Item.getItemId(event.item());
		if (itemid == null)
			return;
		if (itemid.equals(Item.GAME_PSERVER.getItemId())) {
			UserWrapper.fromUser(event.user())
					.setOpenInventory(new InventoryGameServersSelection(event.user()));
		} else if (itemid.equals(Item.WORLD_PSERVER.getItemId())) {
			UserWrapper.fromUser(event.user()).setOpenInventory(
					new InventoryLoading(Component.text("Creating Server..."), event.user(), u -> {
						try {
							PServerExecutor ex =
									new PServerBuilder().type(Type.WORLD).create().get();
							ex.addOwner(event.user().getUniqueId());
							Thread.sleep(1000);
							return new InventoryPServerConfiguration(event.user(), ex.id());
						} catch (InterruptedException | ExecutionException e) {
							throw new RuntimeException(e);
						}
					}));
		}
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		items.put(this.getPageSize() / 2 - 1, Item.WORLD_PSERVER.getItem(this.user.getUser()));
		items.put(this.getPageSize() / 2 + 1, Item.GAME_PSERVER.getItem(this.user.getUser()));
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5),
				Item.INVENTORY_NEW_PSERVER.getItem(this.user.getUser()));
		super.insertFallbackItems();
	}

}
