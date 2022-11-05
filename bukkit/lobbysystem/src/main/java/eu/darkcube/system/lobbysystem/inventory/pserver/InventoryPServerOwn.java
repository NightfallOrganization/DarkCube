package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.inventory.abstraction.DefaultPagedInventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.PagedInventory;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.UUIDManager;

public class InventoryPServerOwn extends DefaultPagedInventory {

	public static final String META_KEY_SLOT = "lobbysystem.pserver.own.slot";

	private static final String PSERVER_COUNT_PERMISSION = "pserver.own.count.";

	public static final String ITEMID_EXISTING = "InventoryPServerOwnPServerSlotExisting";

	public InventoryPServerOwn(User user) {
		super(Item.PSERVER_OWN_MENU.getDisplayName(user), Item.PSERVER_OWN_MENU, InventoryType.PSERVER_OWN,
				PagedInventory.box(3, 3, 4, 7));
	}

	@Override
	protected Map<Integer, ItemStack> contents(User user) {
		Player p = UUIDManager.getPlayerByUUID(user.getUniqueId());
		int pservercount = 0;
		for (PermissionAttachmentInfo info : p.getEffectivePermissions()) {
			if (info.getValue()) {
				if (info.getPermission().startsWith(InventoryPServerOwn.PSERVER_COUNT_PERMISSION)) {
					try {
						pservercount = Math.max(pservercount, Integer.parseInt(
								info.getPermission().substring(InventoryPServerOwn.PSERVER_COUNT_PERMISSION.length())));
					} catch (Exception ex) {
					}
				}
			}
		}
		final int pagesize = this.getPageSize();

		Map<Integer, ItemStack> m = new HashMap<>();

		PServerUserSlots slots = user.getSlots();

		for (int slot = 0; slot < pservercount; slot++) {
			PServerUserSlot pslot = slots.getSlot(slot);
			ItemBuilder item = PServerDataManager.getDisplayItem(user, pslot);

			if (item == null) {
				item = new ItemBuilder(Item.INVENTORY_PSERVER_SLOT_EMPTY.getItem(user));
				item.meta(null);
			} else {
				item.build();
				item.meta(null);
				Item.setItemId(item, InventoryPServerOwn.ITEMID_EXISTING);
			}

			ItemBuilder.Unsafe unsafe = item.getUnsafe();
			unsafe.setInt(InventoryPServerOwn.META_KEY_SLOT, slot);

			m.put(slot, item.build());
		}
		for (int slot = pservercount % pagesize + (pservercount / pagesize) * pagesize; slot < pagesize; slot++) {
			m.put(slot, Item.INVENTORY_PSERVER_SLOT_NOT_BOUGHT.getItem(user));
		}
		return m;
	}

//	private Map<User, Map<Integer, ItemStack>> contents = new HashMap<>();
//
//	@Override
//	protected Map<Integer, ItemStack> getContents(User user) {
//		if (contents.containsKey(user)) {
//			return contents.get(user);
//		}
//		Map<Integer, ItemStack> m = new HashMap<>();
//		for (int i = 0; i < getPageSize(); i++) {
//			m.put(i, Item.LOADING.getItem(user));
//		}
//		return m;
//	}
//
//	@Override
//	protected void onOpen(User user) {
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				Player p = UUIDManager.getPlayerByUUID(user.getUniqueId());
//				int pservercount = 0;
//				for (PermissionAttachmentInfo info : p.getEffectivePermissions()) {
//					if (info.getValue()) {
//						if (info.getPermission().startsWith(PSERVER_COUNT_PERMISSION)) {
//							try {
//								pservercount = Math.max(pservercount, Integer
//										.parseInt(info.getPermission().substring(PSERVER_COUNT_PERMISSION.length())));
//							} catch (Exception ex) {
//							}
//						}
//					}
//				}
//				final int pagesize = getPageSize();
//
//				Map<Integer, ItemStack> m = new HashMap<>();
//
//				PServerUserSlots slots = user.getSlots();
//
//				for (int slot = 0; slot < pservercount; slot++) {
//					PServerUserSlot pslot = slots.getSlot(slot);
//					ItemBuilder item = null;
//
//					if (!pslot.isUsed()) {
//						item = new ItemBuilder(Item.INVENTORY_PSERVER_SLOT_EMPTY.getItem(user));
//					} else {
//						item = new ItemBuilder(Item.PSERVER_SLOT.getItem(user, Integer.toString(slot + 1)));
//					}
//					item.setMeta(null);
//					ItemBuilder.Unsafe unsafe = item.getUnsafe();
//					unsafe.setInt(META_KEY_SLOT, slot);
//
//					m.put(slot, item.build());
//				}
//				for (int slot = pservercount % pagesize
//						+ (pservercount / pagesize) * pagesize; slot < pagesize; slot++) {
//					m.put(slot, Item.INVENTORY_PSERVER_SLOT_NOT_BOUGHT.getItem(user));
//				}
//				contents.put(user, m);
//				update(user);
//			}
//		}.runTaskAsynchronously(Lobby.getInstance());
//	}
//
//	@Override
//	protected void onClose(User user) {
//		contents.remove(user);
//	}

	@Override
	protected void insertDefaultItems(InventoryManager manager) {
		super.insertDefaultItems(manager);
		manager.arrowSlots.put(ArrowType.PREV, new Integer[] {
				Inventory.s(3, 2), Inventory.s(4, 2)
		});
		manager.arrowSlots.put(ArrowType.NEXT, new Integer[] {
				Inventory.s(3, 8), Inventory.s(4, 8)
		});

		manager.setFallbackItem(Inventory.s(1, 4), Item.INVENTORY_PSERVER_PUBLIC.getItem(manager.user));
		manager.setFallbackItem(Inventory.s(1, 5), Item.LIME_GLASS_PANE.getItem(manager.user));
		manager.setFallbackItem(Inventory.s(1, 6), Item.INVENTORY_PSERVER_PRIVATE.getItem(manager.user));
		manager.setFallbackItem(Inventory.s(1, 7), Item.LIME_GLASS_PANE.getItem(manager.user));
	}

}
