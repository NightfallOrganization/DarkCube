/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory.pserver;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.AsyncPagedInventory;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.inventoryapi.v1.PageArrow;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class InventoryPServerOwn extends LobbyAsyncPagedInventory {

	public static final Key META_KEY_PSERVERID =
			new Key(Lobby.getInstance(), "lobbysystem.pserver.own.pserverid");
	public static final String ITEMID_EXISTING = "InventoryPServerOwnPServerSlotExisting";
	private static final InventoryType type_pserver_own = InventoryType.of("pserver_own");
	private static final String PSERVER_COUNT_PERMISSION = "pserver.own.count.";
	private final Listener listener;

	public InventoryPServerOwn(User user) {
		super(InventoryPServerOwn.type_pserver_own, Item.PSERVER_PRIVATE.getDisplayName(user),
				6 * 9, AsyncPagedInventory.box(3, 3, 4, 7), user);
		this.listener = new Listener();
		this.listener.register();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		Player p = user.getUser().asPlayer();
		if (p == null)
			return;
		int pservercount = 0;
		for (PermissionAttachmentInfo info : p.getEffectivePermissions()) {
			if (info.getValue()) {
				if (info.getPermission().startsWith(InventoryPServerOwn.PSERVER_COUNT_PERMISSION)) {
					try {
						pservercount = Math.max(pservercount, Integer.parseInt(info.getPermission()
								.substring(InventoryPServerOwn.PSERVER_COUNT_PERMISSION.length())));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		final int pagesize = this.getPageSize();

		Collection<UniqueId> col =
				PServerProvider.getInstance().getPServerIDs(user.getUser().getUniqueId());
		pservercount = Math.max(pservercount, col.size());
		Iterator<UniqueId> it = col.iterator();

		for (int slot = 0; slot < pservercount; slot++) {
			UniqueId pserverId = it.hasNext() ? it.next() : null;
			ItemBuilder item = PServerDataManager.getDisplayItem(this.user.getUser(), pserverId);

			if (item == null) {
				item = ItemBuilder.item(
						Item.INVENTORY_PSERVER_SLOT_EMPTY.getItem(this.user.getUser()));
			} else {
				Item.setItemId(item, InventoryPServerOwn.ITEMID_EXISTING);
				PServer ps = PServerProvider.getInstance().getPServer(pserverId);
				PServer.State state = ps == null ? PServer.State.OFFLINE : ps.getState();
				Message mstate = state == PServer.State.OFFLINE
						? Message.STATE_OFFLINE
						: state == PServer.State.RUNNING
								? Message.STATE_RUNNING
								: state == PServer.State.STARTING
										? Message.STATE_STARTING
										: state == PServer.State.STOPPING
												? Message.STATE_STOPPING
												: null;
				if (mstate == null)
					throw new IllegalStateException();
				item.lore(Message.PSERVEROWN_STATUS.getMessage(user.getUser(),
						mstate.getMessage(user.getUser())));
			}

			if (pserverId != null)
				item.persistentDataStorage()
						.set(InventoryPServerOwn.META_KEY_PSERVERID, PersistentDataTypes.STRING,
								pserverId.toString());

			items.put(slot, item.build());
		}
		for (int slot = pservercount % pagesize + (pservercount / pagesize) * pagesize;
				slot < pagesize; slot++) {
			items.put(slot, Item.INVENTORY_PSERVER_SLOT_NOT_BOUGHT.getItem(this.user.getUser()));
		}
	}

	@Override
	protected void insertArrowItems() {
		this.arrowSlots.put(PageArrow.PREVIOUS,
				new Integer[] {IInventory.slot(3, 2), IInventory.slot(4, 2)});
		this.arrowSlots.put(PageArrow.NEXT,
				new Integer[] {IInventory.slot(3, 8), IInventory.slot(4, 8)});
		this.arrowItem.put(PageArrow.NEXT, Item.ARROW_NEXT.getItem(user.getUser()));
		this.arrowItem.put(PageArrow.PREVIOUS, Item.ARROW_PREVIOUS.getItem(user.getUser()));
	}

	@Override
	protected void insertDefaultItems0() {
		super.insertDefaultItems0();

		this.fallbackItems.put(IInventory.slot(1, 4),
				Item.INVENTORY_PSERVER_PUBLIC.getItem(this.user.getUser()));
		this.fallbackItems.put(IInventory.slot(1, 5),
				Item.LIME_GLASS_PANE.getItem(this.user.getUser()));
		this.fallbackItems.put(IInventory.slot(1, 6),
				Item.INVENTORY_PSERVER_PRIVATE.getItem(this.user.getUser()));
		this.fallbackItems.put(IInventory.slot(1, 7),
				Item.LIME_GLASS_PANE.getItem(this.user.getUser()));
	}

	@Override
	protected void destroy() {
		super.destroy();
		listener.unregister();
	}

	public class Listener {

		public void register() {
			CloudNetDriver.getInstance().getEventManager().registerListener(this);
		}

		public void unregister() {
			CloudNetDriver.getInstance().getEventManager().unregisterListener(this);
		}

		@EventListener
		public void handle(PServerUpdateEvent event) {
			recalculate();
		}

	}

}
