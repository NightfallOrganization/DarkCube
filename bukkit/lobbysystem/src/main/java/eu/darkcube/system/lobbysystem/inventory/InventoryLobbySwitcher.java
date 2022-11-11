package eu.darkcube.system.lobbysystem.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceId;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryLobbySwitcher extends LobbyAsyncPagedInventory {

	private static final InventoryType type_lobby_switcher = InventoryType.of("lobby_switcher");

//	private static final int[] SLOTS;
//	private static final int[] SORT;
//
//	static {
//		int center = s(1, 5);
//		List<Integer> slots = new ArrayList<>();
//		for (int r = 2; r <= 4; r++) {
//			for (int i = 2; i <= 8; i++) {
//				slots.add(s(r, i));
//			}
//		}
//		SLOTS = new int[slots.size()];
//		SORT = new int[SLOTS.length];
//		for (int i = 0; i < SLOTS.length; i++) {
//			SLOTS[i] = slots.get(i);
//			SORT[i] = dist(center, SLOTS[i]);
//		}
//	}
//	
	public InventoryLobbySwitcher(User user) {
		super(InventoryLobbySwitcher.type_lobby_switcher, Item.INVENTORY_LOBBY_SWITCHER.getDisplayName(user), user);
	}

//	@Override
//	public void playAnimation(User user) {
//		this.animate(user, false);
//	}
//
//	@Override
//	public void skipAnimation(User user) {
//		this.animate(user, true);
//	}

	// @formatter:off
	private static final int[] SLOTS = new int[] {
			31, 30, 32, 29, 33, 28, 34, 
			22, 21, 23, 20, 24, 19, 25, 
			40, 39, 41, 38, 42, 37, 43
	};

	private static final int[] SORT = new int[] {
			02, 03, 03, 04, 04, 05, 05, 
			01, 02, 02, 03, 03, 04, 04, 
			03, 04, 04, 05, 05, 06, 06
	};
	// @formatter:on

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5), Item.INVENTORY_LOBBY_SWITCHER.getItem(this.user));
		super.insertFallbackItems();
	}

	@Override
	protected void insertArrowItems() {
		super.insertArrowItems();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		ServiceId service = Wrapper.getInstance().getServiceId();
		String taskName = service.getTaskName();
		List<ServiceInfoSnapshot> lobbies = CloudNetDriver.getInstance()
				.getCloudServiceProvider()
				.getCloudServices(taskName)
				.stream()
				.filter(ServiceInfoSnapshot::isConnected)
				.filter(s -> s.getProperty(BridgeServiceProperty.IS_ONLINE).orElse(false))
				.sorted(new Comparator<ServiceInfoSnapshot>() {

					@Override
					public int compare(ServiceInfoSnapshot o1, ServiceInfoSnapshot o2) {
						return Integer.compare(o1.getServiceId().getTaskServiceId(),
								o2.getServiceId().getTaskServiceId());
					}

				})
				.collect(Collectors.toList());

		if (lobbies == null || lobbies.isEmpty()) {
			ItemStack item = new ItemStack(Material.BARRIER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§cUnable to load lobbies!");
			this.handle.setItem(InventoryLobbySwitcher.SLOTS[0], item);
			return;
		}
		final ServiceInfoSnapshot thisLobby = Wrapper.getInstance().getCurrentServiceInfoSnapshot();
		final int lobbyCount = lobbies.size();

		final Map<Integer, Map<Integer, ServiceInfoSnapshot>> slots = new HashMap<>();

		final List<Integer> usedSlots = new ArrayList<>();
		for (int i = 0; i < Math.min(InventoryLobbySwitcher.SLOTS.length, lobbyCount); i++) {
			Map<Integer, ServiceInfoSnapshot> m = slots.get(InventoryLobbySwitcher.SORT[i]);
			if (m == null) {
				m = new HashMap<>();
				slots.put(InventoryLobbySwitcher.SORT[i], m);
			}
			usedSlots.add(InventoryLobbySwitcher.SLOTS[i]);
			m.put(InventoryLobbySwitcher.SLOTS[i], lobbies.get(i));
		}
		Collections.sort(usedSlots);

//		final Map<Integer, Map<Integer, ItemStack>> slots1 = new HashMap<>();

		int pointer = 0;
		for (int slot : usedSlots) {
			for (int count : slots.keySet()) {
//				Map<Integer, ItemStack> map = slots1.get(count);
//				if (map == null) {
//					map = new HashMap<>();
//					slots1.put(count, map);
//				}
				for (int oslot : slots.get(count).keySet()) {
					if (oslot != slot) {
						continue;
					}
					ServiceInfoSnapshot s = lobbies.get(pointer++);
					ItemStack item = Item.INVENTORY_LOBBY_SWITCHER_OTHER.getItem(this.user);
					ItemMeta meta = item.getItemMeta();
					String id = s.getServiceId().getName();
					if (id.length() > 0) {
						id = Character.toUpperCase(id.charAt(0)) + id.substring(1, id.length()).replace("-", " ");
					}
					if (s.getServiceId().equals(thisLobby.getServiceId())) {
						item = Item.INVENTORY_LOBBY_SWITCHER_CURRENT.getItem(this.user);
						meta.setDisplayName("§c" + id);
					} else {
						meta.setDisplayName("§a" + id);
					}
					item.setItemMeta(meta);
					item = new ItemBuilder(item).getUnsafe()
							.setString("server", s.getServiceId().getName())
							.builder()
							.build();
					this.pageItems.put(slot, item);
					this.updateSlots.add(slot);
//					map.put(slot, item);
				}
			}
		}
	}

//	private void animate(User user, boolean instant) {
//
//		user.playSound(Sound.NOTE_STICKS, 1, 1);
//		new BukkitRunnable() {
//
//			private ItemStack i1 = Item.LIGHT_GRAY_GLASS_PANE.getItem(user);
//
//			private ItemStack i2 = Item.DARK_GRAY_GLASS_PANE.getItem(user);
//
//			private int count = 0;
//
//			private boolean lobbiesDone = false;
//
//			private void setLobby() {
//				Map<Integer, ItemStack> m = slots1.get(this.count);
//				if (m == null)
//					return;
//				for (int slot : m.keySet()) {
//					InventoryLobbySwitcher.this.handle.setItem(slot, m.get(slot));
//				}
//				slots1.remove(this.count);
//				if (slots1.isEmpty()) {
//					this.lobbiesDone = true;
//				}
//			}
//
//			@Override
//			public void run() {
//				if ((this.count >= 13 && this.lobbiesDone) || this.count >= 22
//						|| user.getOpenInventory() != InventoryLobbySwitcher.this) {
//					this.cancel();
//					return;
//				}
//				if (!instant)
//					user.playSound(Sound.NOTE_STICKS, 1, 1);
//				this.setLobby();
//				switch (this.count) {
//				case 0:
//					InventoryLobbySwitcher.this.handle.setItem(3, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(4, Item.INVENTORY_LOBBY_SWITCHER.getItem(user));
//					InventoryLobbySwitcher.this.handle.setItem(5, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(13, this.i1);
//					break;
//				case 1:
//					InventoryLobbySwitcher.this.handle.setItem(2, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(12, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(14, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(6, this.i1);
//					break;
//				case 2:
//					InventoryLobbySwitcher.this.handle.setItem(1, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(11, this.i2);
//					InventoryLobbySwitcher.this.handle.setItem(15, this.i2);
//					InventoryLobbySwitcher.this.handle.setItem(7, this.i1);
//					break;
//				case 3:
//					InventoryLobbySwitcher.this.handle.setItem(0, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(10, this.i2);
//					InventoryLobbySwitcher.this.handle.setItem(16, this.i2);
//					InventoryLobbySwitcher.this.handle.setItem(8, this.i1);
//					break;
//				case 4:
//					InventoryLobbySwitcher.this.handle.setItem(9, this.i2);
//					InventoryLobbySwitcher.this.handle.setItem(17, this.i2);
//					break;
//				case 5:
//					InventoryLobbySwitcher.this.handle.setItem(18, this.i2);
//					InventoryLobbySwitcher.this.handle.setItem(26, this.i2);
//					break;
//				case 6:
//					InventoryLobbySwitcher.this.handle.setItem(27, this.i2);
//					InventoryLobbySwitcher.this.handle.setItem(35, this.i2);
//					break;
//				case 7:
//					InventoryLobbySwitcher.this.handle.setItem(36, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(44, this.i1);
//					break;
//				case 8:
//					InventoryLobbySwitcher.this.handle.setItem(45, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(53, this.i1);
//					break;
//				case 9:
//					InventoryLobbySwitcher.this.handle.setItem(46, this.i1);
//					InventoryLobbySwitcher.this.handle.setItem(52, this.i1);
//					break;
//				case 10:
//					InventoryLobbySwitcher.this.handle.setItem(47, this.i2);
//					InventoryLobbySwitcher.this.handle.setItem(51, this.i2);
//					break;
//				case 11:
//					InventoryLobbySwitcher.this.handle.setItem(48, this.i2);
//					InventoryLobbySwitcher.this.handle.setItem(50, this.i2);
//					break;
//				case 12:
//					InventoryLobbySwitcher.this.handle.setItem(49, this.i2);
//					break;
//				default:
//					break;
//				}
//				this.count++;
//				if (instant)
//					this.run();
//			}
//
//		}.runTaskTimer(Lobby.getInstance(), 3, 1);
//	}

}