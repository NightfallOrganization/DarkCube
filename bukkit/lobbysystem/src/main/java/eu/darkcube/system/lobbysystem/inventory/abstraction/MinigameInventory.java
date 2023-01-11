/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory.abstraction;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceInfoUpdateEvent;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.MinigameServerSortingInfo;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.GameState;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class MinigameInventory extends LobbyAsyncPagedInventory {
	public static final Key minigameServer = new Key(Lobby.getInstance(), "minigameserver");
	private boolean done = false;
	private Item minigameItem;
	private Listener listener = new Listener();

	public MinigameInventory(Component title, Item minigameItem, InventoryType type, User user) {
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
	protected final void destroy() {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(this.listener);
		this.destroy0();
		super.destroy();
	}

	protected void destroy0() {}

	@SuppressWarnings("deprecation")
	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);

		Collection<ServiceInfoSnapshot> servers = new HashSet<>();
		this.getCloudTasks().forEach(task -> servers.addAll(
				CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(task)));

		Map<ServiceInfoSnapshot, GameState> states = new HashMap<>();
		for (ServiceInfoSnapshot server : new HashSet<>(servers)) {
			try {
				GameState state = GameState.fromString(
						server.getProperty(BridgeServiceProperty.STATE).orElse(null));
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
			ItemBuilder builder = ItemBuilder.item(Material.STAINED_CLAY);
			builder.amount(online);
			builder.displayname(motd);
			builder.lore("§7Spieler: " + online + "/" + maxPlayers);
			if (state == GameState.LOBBY) {
				if (online == 0) {
					builder.damage(DyeColor.GRAY.getWoolData());
				} else {
					builder.damage(DyeColor.LIME.getWoolData());
				}
			} else if (state == GameState.INGAME) {
				builder.damage(DyeColor.ORANGE.getWoolData());
			} else if (state == GameState.STOPPING) {
				builder.damage(DyeColor.RED.getWoolData());
			} else if (state == GameState.UNKNOWN) {
				builder.damage(DyeColor.RED.getWoolData());
			}
			ItemStack item = builder.build();
			item = ItemBuilder.item(item).persistentDataStorage()
					.iset(minigameServer, PersistentDataTypes.STRING,
							server.getServiceId().getUniqueId().toString()).builder().build();
			ItemSortingInfo info = new ItemSortingInfo(item, online, maxPlayers, state);
			itemSortingInfos.add(info);
		}

		Collections.sort(itemSortingInfos);
		int size = itemSortingInfos.size();
		for (int slot = 0; slot < size; slot++) {
			items.put(slot, itemSortingInfos.get(slot).getItem());
		}
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5),
				this.minigameItem.getItem(this.user.getUser()));
		super.insertFallbackItems();
	}

	protected static class ItemSortingInfo implements Comparable<ItemSortingInfo> {

		private ItemStack item;
		private MinigameServerSortingInfo minigame;

		public ItemSortingInfo(ItemStack item, int onPlayers, int maxPlayers, GameState state) {
			super();
			this.item = item;
			this.minigame = new MinigameServerSortingInfo(onPlayers, maxPlayers, state);
		}

		@Override
		public int compareTo(@NotNull ItemSortingInfo other) {
			int amt = minigame.compareTo(other.minigame);
			if (amt == 0) {
				amt = this.getDisplay().orElse("").compareTo(other.getDisplay().orElse(""));
			}
			return amt;
		}

		private Optional<String> getDisplay() {
			if (this.item.hasItemMeta()) {
				return Optional.ofNullable(this.item.getItemMeta().getDisplayName());
			}
			return Optional.empty();
		}

		public ItemStack getItem() {
			return ItemBuilder.item(this.item).build();
		}

	}


	public class Listener {

		@EventListener
		public void handle(CloudServiceInfoUpdateEvent event) {
			MinigameInventory.this.recalculate();
		}

	}

}
