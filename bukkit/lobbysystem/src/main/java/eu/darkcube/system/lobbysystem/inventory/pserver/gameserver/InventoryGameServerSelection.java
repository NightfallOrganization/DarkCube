/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory.pserver.gameserver;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class InventoryGameServerSelection extends LobbyAsyncPagedInventory {

	public static final String ITEMID = "pserver_gameserver";
	public static final Key GAMESERVER_META_KEY =
			new Key(Lobby.getInstance(), "pserver.gameserver");
	public static final String SLOT = "slot";
	public static final String SERVICETASK = "serviceTask";
	protected final int[] itemSort;
	private final Item item;
	private final Supplier<Collection<ServiceTask>> supplier;
	private final BiFunction<User, ServiceTask, ItemBuilder> toItemFunction;
	private final User auser;
	private boolean done = false;

	public InventoryGameServerSelection(User user, Item item, InventoryType type,
			Supplier<Collection<ServiceTask>> supplier,
			BiFunction<User, ServiceTask, ItemBuilder> toItemFunction) {
		super(type, item.getDisplayName(user), UserWrapper.fromUser(user));
		auser = user;
		this.supplier = supplier;
		this.toItemFunction = toItemFunction;
		this.item = item;
		this.itemSort = new int[] {
				//@formatter:off
				10, 9, 11, 8, 12, 3, 17, 
				16, 18, 2, 4, 7, 13, 15,
				19, 1, 5, 14, 20, 0, 6
				//@formatter:on
		};
		this.done = true;
		this.complete();
	}

	@Override
	protected boolean done() {
		return super.done() && this.done;
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		int slot = 0;
		Collection<ServiceTask> serviceTasks = this.supplier.get();
		for (ServiceTask serviceTask : serviceTasks) {
			ItemBuilder b = this.toItemFunction.apply(this.auser, serviceTask);
			JsonObject data = new JsonObject();
			data.addProperty(InventoryGameServerSelection.SLOT, slot);
			data.addProperty(InventoryGameServerSelection.SERVICETASK, serviceTask.getName());
			b.persistentDataStorage().set(InventoryGameServerSelection.GAMESERVER_META_KEY,
					PersistentDataTypes.STRING, data.toString());
			Item.setItemId(b, InventoryGameServerSelection.ITEMID);
			items.put(this.itemSort[slot % this.itemSort.length] + this.itemSort.length * (slot
					/ this.itemSort.length), b.build());
			slot++;
		}
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5), this.item.getItem(this.auser));
		super.insertFallbackItems();
	}
}
