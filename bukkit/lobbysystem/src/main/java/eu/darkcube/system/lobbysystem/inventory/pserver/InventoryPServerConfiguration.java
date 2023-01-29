/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory.pserver;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.userapi.User;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryPServerConfiguration extends LobbyAsyncPagedInventory {

	private static final InventoryType type_pserver_configuration =
			InventoryType.of("type_pserver_configuration");

	public final UniqueId pserverId;

	private boolean done = false;

	public InventoryPServerConfiguration(User user, UniqueId pserverId) {
		super(type_pserver_configuration, getDisplayName(user, pserverId), user);
		this.pserverId = pserverId;
		CloudNetDriver.getInstance().getEventManager().registerListener(this);
		this.done = true;
		this.complete();
	}

	private static Component getDisplayName(User user, UniqueId pserverId) {
		ItemBuilder item = PServerDataManager.getDisplayItem(user, pserverId);
		return item == null ? null : item.displayname();
	}

	@Override
	protected boolean done() {
		return super.done() && this.done;
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		items.put(8, Item.PSERVER_DELETE.getItem(this.user.getUser()));
		PServer ps = PServerProvider.getInstance().getPServer(pserverId);
		State state = ps == null ? State.OFFLINE : ps.getState();
		if (state == State.OFFLINE) {
			items.put(12, Item.START_PSERVER.getItem(this.user.getUser()));
		} else {
			items.put(12, Item.STOP_PSERVER.getItem(this.user.getUser()));
		}
		JsonDocument data = PServerProvider.getInstance().getPServerData(pserverId);
		if (data.getBoolean("private", false)) {
			items.put(10, Item.PSERVER_PRIVATE.getItem(user.getUser()));
		} else {
			items.put(10, Item.PSERVER_PUBLIC.getItem(user.getUser()));
		}
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5),
				PServerDataManager.getDisplayItem(this.user.getUser(), pserverId).build());
		super.insertFallbackItems();
	}

	@Override
	protected void destroy() {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(this);
	}

	@EventListener
	public void handle(PServerUpdateEvent event) {
		if (!event.getPServer().getId().equals(pserverId)) {
			return;
		}
		this.recalculate();
	}

}
