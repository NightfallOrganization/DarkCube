/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceId;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryLobbySwitcher extends LobbyAsyncPagedInventory {
	public static final Key server = new Key(Lobby.getInstance(), "server");
	private static final InventoryType type_lobby_switcher = InventoryType.of("lobby_switcher");
	// @formatter:off
	private static final int[] SLOTS = new int[] {
			31, 30, 32, 29, 33, 28, 34,
			22, 21, 23, 20, 24, 19, 25,
			40, 39, 41, 38, 42, 37, 43
	};

	// @Override
	// public void playAnimation(User user) {
	// this.animate(user, false);
	// }
	//
	// @Override
	// public void skipAnimation(User user) {
	// this.animate(user, true);
	// }

	// private static final int[] SLOTS;
	// private static final int[] SORT;
	//
	// static {
	// int center = s(1, 5);
	// List<Integer> slots = new ArrayList<>();
	// for (int r = 2; r <= 4; r++) {
	// for (int i = 2; i <= 8; i++) {
	// slots.add(s(r, i));
	// }
	// }
	// SLOTS = new int[slots.size()];
	// SORT = new int[SLOTS.length];
	// for (int i = 0; i < SLOTS.length; i++) {
	// SLOTS[i] = slots.get(i);
	// SORT[i] = dist(center, SLOTS[i]);
	// }
	// }
	//
	public InventoryLobbySwitcher(User user) {
		super(InventoryLobbySwitcher.type_lobby_switcher,
				Item.INVENTORY_LOBBY_SWITCHER.getDisplayName(user), user);
		System.arraycopy(InventoryLobbySwitcher.SLOTS,0,this.PAGE_SLOTS,0,InventoryLobbySwitcher.SLOTS.length);
	}

//	private static final int[] SORT = new int[] {
//			02, 03, 03, 04, 04, 05, 05, 
//			01, 02, 02, 03, 03, 04, 04, 
//			03, 04, 04, 05, 05, 06, 06
//	};
	// @formatter:on

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		ServiceId service = Wrapper.getInstance().getServiceId();
		String taskName = service.getTaskName();
		List<ServiceInfoSnapshot> lobbies =
				CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(taskName)
						.stream().filter(ServiceInfoSnapshot::isConnected)
						.filter(s -> s.getProperty(BridgeServiceProperty.IS_ONLINE).orElse(false))
						.sorted(new Comparator<ServiceInfoSnapshot>() {

							@Override
							public int compare(ServiceInfoSnapshot o1, ServiceInfoSnapshot o2) {
								return Integer.compare(o1.getServiceId().getTaskServiceId(),
										o2.getServiceId().getTaskServiceId());
							}

						}).collect(Collectors.toList());

		if (lobbies == null || lobbies.isEmpty()) {
			ItemStack item = new ItemStack(Material.BARRIER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§cUnable to load lobbies!");
			items.put(0, item);
			return;
		}
		final ServiceInfoSnapshot thisLobby = Wrapper.getInstance().getCurrentServiceInfoSnapshot();
		final int lobbyCount = lobbies.size();

		for (int i = 0; i < Math.min(InventoryLobbySwitcher.SLOTS.length, lobbyCount); i++) {
			ServiceInfoSnapshot s = lobbies.get(i);
			ItemStack item = Item.INVENTORY_LOBBY_SWITCHER_OTHER.getItem(this.user.getUser());
			ItemMeta meta = item.getItemMeta();
			String id = s.getServiceId().getName();
			if (id.length() > 0) {
				id = Character.toUpperCase(id.charAt(0)) + id.substring(1)
						.replace("-", " ");
			}
			if (s.getServiceId().equals(thisLobby.getServiceId())) {
				item = Item.INVENTORY_LOBBY_SWITCHER_CURRENT.getItem(this.user.getUser());
				meta.setDisplayName("§c" + id);
			} else {
				meta.setDisplayName("§a" + id);
			}
			item.setItemMeta(meta);
			item = ItemBuilder.item(item).persistentDataStorage()
					.iset(server, PersistentDataTypes.STRING, s.getServiceId().getName()).builder()
					.build();
			items.put(i, item);
		}

		// int pointer = 0;
		// ServiceInfoSnapshot s = lobbies.get(pointer++);
		// ItemStack item = Item.INVENTORY_LOBBY_SWITCHER_OTHER.getItem(this.user);
		// ItemMeta meta = item.getItemMeta();
		// String id = s.getServiceId().getName();
		// if (id.length() > 0) {
		// id = Character.toUpperCase(id.charAt(0))
		// + id.substring(1, id.length()).replace("-", " ");
		// }
		// if (s.getServiceId().equals(thisLobby.getServiceId())) {
		// item = Item.INVENTORY_LOBBY_SWITCHER_CURRENT.getItem(this.user);
		// meta.setDisplayName("§c" + id);
		// } else {
		// meta.setDisplayName("§a" + id);
		// }
		// item.setItemMeta(meta);
		// item = new ItemBuilder(
		// item).getUnsafe().setString("server", s.getServiceId().getName()).builder().build();
		// items.put(slot, item);
	}

	@Override
	protected void insertArrowItems() {
		super.insertArrowItems();
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5),
				Item.INVENTORY_LOBBY_SWITCHER.getItem(this.user.getUser()));
		super.insertFallbackItems();
	}

	// private void animate(User user, boolean instant) {
	//
	// user.playSound(Sound.NOTE_STICKS, 1, 1);
	// new BukkitRunnable() {
	//
	// private ItemStack i1 = Item.LIGHT_GRAY_GLASS_PANE.getItem(user);
	//
	// private ItemStack i2 = Item.DARK_GRAY_GLASS_PANE.getItem(user);
	//
	// private int count = 0;
	//
	// private boolean lobbiesDone = false;
	//
	// private void setLobby() {
	// Map<Integer, ItemStack> m = slots1.get(this.count);
	// if (m == null)
	// return;
	// for (int slot : m.keySet()) {
	// InventoryLobbySwitcher.this.handle.setItem(slot, m.get(slot));
	// }
	// slots1.remove(this.count);
	// if (slots1.isEmpty()) {
	// this.lobbiesDone = true;
	// }
	// }
	//
	// @Override
	// public void run() {
	// if ((this.count >= 13 && this.lobbiesDone) || this.count >= 22
	// || user.getOpenInventory() != InventoryLobbySwitcher.this) {
	// this.cancel();
	// return;
	// }
	// if (!instant)
	// user.playSound(Sound.NOTE_STICKS, 1, 1);
	// this.setLobby();
	// switch (this.count) {
	// case 0:
	// InventoryLobbySwitcher.this.handle.setItem(3, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(4, Item.INVENTORY_LOBBY_SWITCHER.getItem(user));
	// InventoryLobbySwitcher.this.handle.setItem(5, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(13, this.i1);
	// break;
	// case 1:
	// InventoryLobbySwitcher.this.handle.setItem(2, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(12, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(14, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(6, this.i1);
	// break;
	// case 2:
	// InventoryLobbySwitcher.this.handle.setItem(1, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(11, this.i2);
	// InventoryLobbySwitcher.this.handle.setItem(15, this.i2);
	// InventoryLobbySwitcher.this.handle.setItem(7, this.i1);
	// break;
	// case 3:
	// InventoryLobbySwitcher.this.handle.setItem(0, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(10, this.i2);
	// InventoryLobbySwitcher.this.handle.setItem(16, this.i2);
	// InventoryLobbySwitcher.this.handle.setItem(8, this.i1);
	// break;
	// case 4:
	// InventoryLobbySwitcher.this.handle.setItem(9, this.i2);
	// InventoryLobbySwitcher.this.handle.setItem(17, this.i2);
	// break;
	// case 5:
	// InventoryLobbySwitcher.this.handle.setItem(18, this.i2);
	// InventoryLobbySwitcher.this.handle.setItem(26, this.i2);
	// break;
	// case 6:
	// InventoryLobbySwitcher.this.handle.setItem(27, this.i2);
	// InventoryLobbySwitcher.this.handle.setItem(35, this.i2);
	// break;
	// case 7:
	// InventoryLobbySwitcher.this.handle.setItem(36, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(44, this.i1);
	// break;
	// case 8:
	// InventoryLobbySwitcher.this.handle.setItem(45, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(53, this.i1);
	// break;
	// case 9:
	// InventoryLobbySwitcher.this.handle.setItem(46, this.i1);
	// InventoryLobbySwitcher.this.handle.setItem(52, this.i1);
	// break;
	// case 10:
	// InventoryLobbySwitcher.this.handle.setItem(47, this.i2);
	// InventoryLobbySwitcher.this.handle.setItem(51, this.i2);
	// break;
	// case 11:
	// InventoryLobbySwitcher.this.handle.setItem(48, this.i2);
	// InventoryLobbySwitcher.this.handle.setItem(50, this.i2);
	// break;
	// case 12:
	// InventoryLobbySwitcher.this.handle.setItem(49, this.i2);
	// break;
	// default:
	// break;
	// }
	// this.count++;
	// if (instant)
	// this.run();
	// }
	//
	// }.runTaskTimer(Lobby.getInstance(), 3, 1);
	// }

}
