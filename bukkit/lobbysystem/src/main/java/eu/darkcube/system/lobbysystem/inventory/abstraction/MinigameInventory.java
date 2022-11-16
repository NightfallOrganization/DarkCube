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
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceInfoUpdateEvent;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import eu.darkcube.system.GameState;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public abstract class MinigameInventory extends LobbyAsyncPagedInventory

{

	private boolean done = false;

	private Item minigameItem;

	private Listener listener = new Listener();

	public MinigameInventory(String title, Item minigameItem, InventoryType type, User user) {
		super(type, title, user);
		this.minigameItem = minigameItem;
		this.done = true;
		this.complete();
		CloudNetDriver.getInstance().getEventManager().registerListener(this.listener);
	}

	protected abstract Set<String> getCloudTasks();

	@Override
	protected boolean done() {
		return super.done() && this.done;
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5), this.minigameItem.getItem(this.user));
		super.insertFallbackItems();
	}

	@Override
	protected final void destroy() {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(this.listener);
		this.destroy0();
		super.destroy();
	}

	protected void destroy0() {
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);

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
				if (state == GameState.STOPPING) {
					continue;
				}
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
				if (json == null)
					continue;
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
		int size = itemSortingInfos.size();
		for (int slot = 0; slot < size; slot++) {
			items.put(slot, itemSortingInfos.get(slot).getItem());
		}
	}

	public class Listener {

		@EventListener
		public void handle(CloudServiceInfoUpdateEvent event) {
			MinigameInventory.this.recalculate();
		}

	}

	protected class ItemSortingInfo implements Comparable<ItemSortingInfo> {

		private ItemStack item;

		private int onPlayers;

		private int maxPlayers;

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
					return -1;
				amt = Integer.compare(other.onPlayers, this.onPlayers);
				break;
			case INGAME:
				if (other.state == GameState.LOBBY)
					return 1;
				if (other.state != GameState.LOBBY && other.state != GameState.INGAME)
					return -1;
				amt = Integer.compare(other.onPlayers, this.onPlayers);
				break;
			default:
				if (other.state != GameState.UNKNOWN)
					return 1;
				amt = Integer.compare(other.onPlayers, this.onPlayers);
				break;
			}
			if (amt == 0) {
				amt = Integer.compare(other.onPlayers, this.onPlayers);
				if (amt != 0) {
					return amt;
				}
				amt = Integer.compare(this.maxPlayers, other.maxPlayers);
				if (amt == 0) {
					amt = this.getDisplay().orElse("").compareTo(other.getDisplay().orElse(""));
				}
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
