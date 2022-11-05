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

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import eu.darkcube.system.GameState;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public abstract class MinigameInventory extends DefaultPagedInventory {

	private Item minigameItem;
//	private Map<User, BukkitRunnable> runnables = new HashMap<>();
//	private Map<User, Map<Integer, ItemStack>> contents = new HashMap<>();

	public MinigameInventory(String title, Item minigameItem, InventoryType type) {
		super(title, type);
		this.minigameItem = minigameItem;
	}

	protected abstract Set<String> getCloudTasks();

	@SuppressWarnings("deprecation")
	@Override
	protected Map<Integer, ItemStack> contents(User user) {
		Collection<ServiceInfoSnapshot> servers = new HashSet<>();
		this.getCloudTasks()
				.stream()
				.forEach(task -> servers
						.addAll(CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(task)));

		Map<ServiceInfoSnapshot, GameState> states = new HashMap<>();
		for (ServiceInfoSnapshot server : new HashSet<>(servers)) {
			try {
				GameState state = GameState.fromString(server.getProperty(BridgeServiceProperty.STATE).orElse(null));
				if (state == null || state == GameState.UNKNOWN)
					throw new NullPointerException();
				states.put(server, state);
			} catch (Exception ex) {
				servers.remove(server);
			}
		}
		List<ItemSortingInfo> itemSortingInfos = new ArrayList<>();
		for (ServiceInfoSnapshot server : new HashSet<>(servers)) {
			String extraText = server.getProperty(BridgeServiceProperty.EXTRA).orElse(null);

			int online = server.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(-1);
			int maxPlayers = server.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(-1);
			try {
				JsonObject json = new Gson().fromJson(extraText, JsonObject.class);
				online = json.getAsJsonPrimitive("online").getAsInt();
				maxPlayers = json.getAsJsonPrimitive("max").getAsInt();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			GameState state = states.get(server);
			String motd = server.getProperty(BridgeServiceProperty.MOTD).orElse(null);
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

		Map<Integer, ItemStack> m = new HashMap<>();
		for (int slot = 0; slot < itemSortingInfos.size(); slot++) {
			ItemSortingInfo info = itemSortingInfos.get(slot);
			m.put(slot, info.getItem());
		}
		return m;
	}

//	@Override
//	protected void onOpen(User user) {
//		runnables.put(user, new BukkitRunnable() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void run() {
//				Collection<ServiceInfoSnapshot> servers = new HashSet<>();
//				getCloudTasks().stream().forEach(
//						task -> servers.addAll(CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(task)));
//
//				Map<ServiceInfoSnapshot, GameState> states = new HashMap<>();
//				for (ServiceInfoSnapshot server : new HashSet<>(servers)) {
//					try {
//						GameState state = GameState.fromString(ServiceInfoSnapshotUtil.getState(server));
//						if (state == null || state == GameState.UNKNOWN)
//							throw new NullPointerException();
//						states.put(server, state);
//					} catch (Exception ex) {
//						servers.remove(server);
//					}
//				}
//				List<ItemSortingInfo> itemSortingInfos = new ArrayList<>();
//				for (ServiceInfoSnapshot server : new HashSet<>(servers)) {
//					String extraText = ServiceInfoSnapshotUtil.getExtra(server);
//
//					int online = ServiceInfoSnapshotUtil.getOnlineCount(server);
//					int maxPlayers = ServiceInfoSnapshotUtil.getMaxPlayers(server);
//					try {
//						JsonObject json = new Gson().fromJson(extraText, JsonObject.class);
//						online = json.getAsJsonPrimitive("online").getAsInt();
//						maxPlayers = json.getAsJsonPrimitive("max").getAsInt();
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//					GameState state = states.get(server);
//					String motd = ServiceInfoSnapshotUtil.getMotd(server);
//					if (motd == null || motd.contains("§cLoading...")) {
//						servers.remove(server);
//						states.remove(server);
//						continue;
//					}
//					ItemBuilder builder = new ItemBuilder(Material.STAINED_CLAY);
//					builder.unsafeStackSize(true);
//					builder.setAmount(online);
//					builder.setDisplayName(motd);
//					builder.addLore("§7Spieler: " + online + "/" + maxPlayers);
//					if (state == GameState.LOBBY) {
//						builder.setDurability(DyeColor.LIME.getWoolData());
//					} else if (state == GameState.INGAME) {
//						builder.setDurability(DyeColor.ORANGE.getWoolData());
//					} else if (state == GameState.STOPPING) {
//						builder.setDurability(DyeColor.RED.getWoolData());
//					} else if (state == GameState.UNKNOWN) {
//						builder.setDurability(DyeColor.RED.getWoolData());
//					}
//					ItemStack item = builder.build();
//					item = new ItemBuilder(item).getUnsafe()
//							.setString("minigameServer", server.getServiceId().getUniqueId().toString()).builder()
//							.getItemStack();
//					ItemSortingInfo info = new ItemSortingInfo(item, online, maxPlayers, state);
//					itemSortingInfos.add(info);
//				}
//
//				Collections.sort(itemSortingInfos);
//
//				Map<Integer, ItemStack> m = new HashMap<>();
//				for (int slot = 0; slot < itemSortingInfos.size(); slot++) {
//					ItemSortingInfo info = itemSortingInfos.get(slot);
//					m.put(slot, info.getItem());
//				}
//				contents.put(user, m);
//				update(user);
//
//			}
//		});
//		runnables.get(user).runTaskAsynchronously(Lobby.getInstance());
//	}
//
//	@Override
//	protected void onClose(User user) {
//		contents.remove(user);
//		runnables.remove(user).cancel();
//	}
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

	@Override
	protected void insertDefaultItems(InventoryManager manager) {
		super.insertDefaultItems(manager);
		manager.setFallbackItem(Inventory.s(1, 5), this.minigameItem.getItem(manager.user));
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
