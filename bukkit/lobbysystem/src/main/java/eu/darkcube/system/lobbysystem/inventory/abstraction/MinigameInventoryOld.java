package eu.darkcube.system.lobbysystem.inventory.abstraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.ServiceInfoSnapshotUtil;
import eu.darkcube.system.GameState;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public abstract class MinigameInventoryOld extends Inventory {

	public MinigameInventoryOld(String displayName, InventoryType type) {
		super(Bukkit.createInventory(null, 6 * 9, displayName), type);
	}

	private static final int[] SLOTS = new int[] {
			10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42,
			43
	};

	private static final int[] SORT = new int[] {
			04, 03, 02, 01, 02, 03, 04, 05, 04, 03, 02, 03, 04, 05, 06, 05, 04, 03, 04, 05, 06, 07, 06, 05, 04, 05, 06,
			07
	};

	@Override
	public void playAnimation(User user) {
		this.animate(user, false);
	}

	@Override
	public void skipAnimation(User user) {
		this.animate(user, true);
	}

	protected abstract ItemStack getMinigameItem(User user);

	protected abstract Set<String> getCloudTasks();

	@SuppressWarnings("deprecation")
	private void calculateItems(Map<Integer, Map<Integer, ItemStack>> items) {
		items.clear();
		Collection<ServiceInfoSnapshot> servers = new HashSet<>();
		this.getCloudTasks()
				.stream()
				.forEach(task -> servers
						.addAll(CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(task)));

		Map<ServiceInfoSnapshot, GameState> states = new HashMap<>();
		for (ServiceInfoSnapshot server : new HashSet<>(servers)) {
			try {
				GameState state = GameState.fromString(ServiceInfoSnapshotUtil.getState(server));
				if (state == null || state == GameState.UNKNOWN)
					throw new NullPointerException();
				states.put(server, state);
			} catch (Exception ex) {
				servers.remove(server);
			}
		}

		List<ItemSortingInfo> itemSortingInfos = new ArrayList<>();
		for (ServiceInfoSnapshot server : new HashSet<>(servers)) {
			String extraText = ServiceInfoSnapshotUtil.getExtra(server);

			int online = ServiceInfoSnapshotUtil.getOnlineCount(server);
			int maxPlayers = ServiceInfoSnapshotUtil.getMaxPlayers(server);
			try {
				JsonObject json = new Gson().fromJson(extraText, JsonObject.class);
				online = json.getAsJsonPrimitive("online").getAsInt();
				maxPlayers = json.getAsJsonPrimitive("max").getAsInt();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			GameState state = states.get(server);
			String motd = ServiceInfoSnapshotUtil.getMotd(server);
			if (motd == null || motd.contains("§cLoading...")) {
				servers.remove(server);
				states.remove(server);
				continue;
			}
			ItemBuilder builder = new ItemBuilder(Material.STAINED_CLAY);
			builder.unsafeStackSize(true);
			builder.amount(online);
			builder.displayname(motd);
			builder.lore("§7Spieler: " + online + "/" + maxPlayers);
			if (state == GameState.LOBBY) {
				builder.durability(DyeColor.LIME.getWoolData());
			} else if (state == GameState.INGAME) {
				builder.durability(DyeColor.ORANGE.getWoolData());
			} else if (state == GameState.STOPPING) {
				builder.durability(DyeColor.RED.getWoolData());
			} else if (state == GameState.UNKNOWN) {
				builder.durability(DyeColor.RED.getWoolData());
			}
			ItemStack item = builder.build();
			item = new ItemBuilder(item).getUnsafe()
					.setString("minigameServer", server.getServiceId().getUniqueId().toString())
					.builder()
					.build();
			ItemSortingInfo info = new ItemSortingInfo(item, online, maxPlayers, state);
			itemSortingInfos.add(info);
		}

		Collections.sort(itemSortingInfos);

//		itemSortingInfos.sort((o1, o2) -> o2.compareTo(o1));

		for (int slotId = 0; slotId < Math.min(MinigameInventoryOld.SLOTS.length, itemSortingInfos.size()); slotId++) {
			int slot = MinigameInventoryOld.SLOTS[slotId];
			ItemSortingInfo info = itemSortingInfos.get(slotId);
			Map<Integer, ItemStack> m = items.get(MinigameInventoryOld.SORT[slotId]);
			if (m == null) {
				m = new HashMap<>();
				items.put(MinigameInventoryOld.SORT[slotId], m);
			}
			m.put(slot, info.getItem());
		}
	}

	private void animate(User user, boolean instant) {

		Map<Integer, Map<Integer, ItemStack>> items = new HashMap<>();

		new Runnable(user, items, instant).runTaskTimer(Lobby.getInstance(), 1, 1);
	}

	private class Runnable extends BukkitRunnable {

		private Map<Integer, Map<Integer, ItemStack>> items;

		private User user;

		private ItemStack i1;

		private ItemStack i2;

		private Set<Integer> usedSlots = new HashSet<>();

		private boolean instant;

		private int count = 0;

		public Runnable(User user, Map<Integer, Map<Integer, ItemStack>> items, boolean instant) {
			this.user = user;
			this.instant = instant;
			this.items = items;
			this.i1 = Item.LIGHT_GRAY_GLASS_PANE.getItem(user);
			this.i2 = Item.DARK_GRAY_GLASS_PANE.getItem(user);
			if (instant) {
				user.playSound(Sound.NOTE_STICKS, 1, 1);
			}
		}

		private void setItems() {
			Map<Integer, ItemStack> m = this.items.get(this.count);
			if (m == null)
				return;
			for (int slot : m.keySet()) {
				MinigameInventoryOld.this.handle.setItem(slot, m.get(slot));
				this.usedSlots.add(slot);
			}
		}

		private void resetItems() {
			for (int slot : this.usedSlots) {
				MinigameInventoryOld.this.handle.setItem(slot, null);
			}
			this.usedSlots.clear();
			for (int id : this.items.keySet()) {
				Map<Integer, ItemStack> m = this.items.get(id);
				for (int slot : m.keySet()) {
					MinigameInventoryOld.this.handle.setItem(slot, m.get(slot));
					this.usedSlots.add(slot);
				}
			}
		}

		@Override
		public void run() {
			if (this.user.getOpenInventory() != MinigameInventoryOld.this) {
				this.cancel();
				return;
			}
			if (this.count % 30 == 0) {
				MinigameInventoryOld.this.calculateItems(this.items);
				if (this.count > 0)
					this.resetItems();
			}
			if (this.count <= 13) {
				this.setItems();
				if (!this.instant)
					this.user.playSound(Sound.NOTE_STICKS, 1, 1);
			}
			switch (this.count) {
			case 0:
				MinigameInventoryOld.this.handle.setItem(4, MinigameInventoryOld.this.getMinigameItem(this.user));
				break;
			case 1:
				MinigameInventoryOld.this.handle.setItem(3, this.i1);
				MinigameInventoryOld.this.handle.setItem(5, this.i1);
				break;
			case 2:
				MinigameInventoryOld.this.handle.setItem(2, this.i1);
				MinigameInventoryOld.this.handle.setItem(6, this.i1);
				break;
			case 3:
				MinigameInventoryOld.this.handle.setItem(1, this.i1);
				MinigameInventoryOld.this.handle.setItem(7, this.i1);
				break;
			case 4:
				MinigameInventoryOld.this.handle.setItem(0, this.i1);
				MinigameInventoryOld.this.handle.setItem(8, this.i1);
				break;
			case 5:
				MinigameInventoryOld.this.handle.setItem(9, this.i2);
				MinigameInventoryOld.this.handle.setItem(17, this.i2);
				break;
			case 6:
				MinigameInventoryOld.this.handle.setItem(18, this.i1);
				MinigameInventoryOld.this.handle.setItem(26, this.i1);
				break;
			case 7:
				MinigameInventoryOld.this.handle.setItem(27, this.i2);
				MinigameInventoryOld.this.handle.setItem(35, this.i2);
				break;
			case 8:
				MinigameInventoryOld.this.handle.setItem(36, this.i1);
				MinigameInventoryOld.this.handle.setItem(44, this.i1);
				break;
			case 9:
				MinigameInventoryOld.this.handle.setItem(45, this.i1);
				MinigameInventoryOld.this.handle.setItem(53, this.i1);
				break;
			case 10:
				MinigameInventoryOld.this.handle.setItem(46, this.i1);
				MinigameInventoryOld.this.handle.setItem(52, this.i1);
				break;
			case 11:
				MinigameInventoryOld.this.handle.setItem(47, this.i2);
				MinigameInventoryOld.this.handle.setItem(51, this.i2);
				break;
			case 12:
				MinigameInventoryOld.this.handle.setItem(48, this.i2);
				MinigameInventoryOld.this.handle.setItem(50, this.i2);
				break;
			case 13:
				MinigameInventoryOld.this.handle.setItem(49, this.i2);
				break;
			default:
				break;
			}
			this.count++;
			if (this.instant && this.count <= 13) {
				this.run();
			}
		}

	}

	protected class ItemSortingInfo implements Comparable<ItemSortingInfo> {

		private ItemStack item;

		private Integer onPlayers;

		private Integer maxPlayers;

		private GameState state;

		public ItemSortingInfo(ItemStack item, int onPlayers, int maxPlayers, GameState state) {
			super();
			this.item = item;
			this.onPlayers = onPlayers;
			this.maxPlayers = maxPlayers;
			this.state = state;
		}

		@Override
		public int compareTo(ItemSortingInfo other) {
			int amt = 0;
			switch (this.state) {
			case LOBBY:
				if (other.state != GameState.LOBBY)
					return 1;
				amt = Integer.compare(this.item.getAmount(), other.item.getAmount());
				break;
			case INGAME:
				if (other.state == GameState.LOBBY)
					return -1;
				if (other.state != GameState.LOBBY && other.state != GameState.INGAME)
					return 1;
				amt = Integer.compare(this.item.getAmount(), other.item.getAmount());
				break;
			case STOPPING:
				if (other.state == GameState.LOBBY || other.state == GameState.INGAME)
					return -1;
				if (other.state == GameState.UNKNOWN)
					return 1;
				amt = Integer.compare(this.item.getAmount(), other.item.getAmount());
				break;
			case UNKNOWN:
				if (other.state != GameState.UNKNOWN)
					return -1;
				amt = Integer.compare(this.item.getAmount(), other.item.getAmount());
				break;
			}
			if (amt == 0) {
				if (this.onPlayers > other.onPlayers) {
					return -1;
				} else if (this.onPlayers < other.onPlayers) {
					return 1;
				}
				amt = this.maxPlayers.compareTo(other.maxPlayers);
				if (amt == 0) {
					amt = this.getDisplay().orElse("").compareTo(other.getDisplay().orElse(""));
				}

//				String display = getDisplay().orElse("");
//				amt = display.compareToIgnoreCase(other.getDisplay().orElse(""));
			}
			return amt;
		}

		private Optional<String> getDisplay() {
			if (this.item.hasItemMeta()) {
				return Optional.ofNullable(this.item.getItemMeta().getDisplayName());
			}
			return Optional.ofNullable(null);
		}

		public ItemStack getItem() {
			return new ItemBuilder(this.item).build();
		}

	}

}
